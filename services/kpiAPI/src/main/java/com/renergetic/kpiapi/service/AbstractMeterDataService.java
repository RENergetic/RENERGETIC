package com.renergetic.kpiapi.service;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.renergetic.kpiapi.dao.MeasurementDAORequest;
import com.renergetic.kpiapi.dao.AbstractMeterDataDAO;
import com.renergetic.kpiapi.exception.HttpRuntimeException;
import com.renergetic.kpiapi.exception.InvalidArgumentException;
import com.renergetic.kpiapi.exception.NotFoundException;
import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.AbstractMeterConfig;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.model.InfluxFunction;
import com.renergetic.kpiapi.repository.AbstractMeterRepository;
import com.renergetic.kpiapi.service.utils.DateConverter;
import com.renergetic.kpiapi.service.utils.HttpAPIs;
import com.renergetic.kpiapi.service.utils.MathCalculator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AbstractMeterDataService {

	@Value("${influx.api.url}")
	private String influxURL;
	
	@Autowired
	private AbstractMeterRepository abstractMeterRepository;

	@Autowired
	private HttpAPIs httpAPIs;
	
	@Autowired
	private MathCalculator calculator;
	
	/**
	 * Retrieves the meter data DAO for the given name, domain, and time range.
	 *
	 * @param  name   the name of the meter data
	 * @param  domain the domain of the meter data
	 * @param  from   the start time of the meter data (null for all time)
	 * @param  to     the end time of the meter data (null for all time)
	 * @return        the AbstractMeterDataDAO object containing the requested data
	 */
	public AbstractMeterDataDAO get(String name, Domain domain, Long from, Long to) {
		
		AbstractMeterDataDAO ret = new AbstractMeterDataDAO();

		ret.setName(AbstractMeter.obtain(name));
		ret.setDomain(domain);

		Map<String, String> params = new HashMap<>();

		// Set parameters to Influx API request
		params.put("measurements", "abstract_meter");
		params.put("measurement_type", ret.getName().name().toLowerCase());
		params.put("domain", domain.name());
		if (from != null)
			params.put("from", from.toString());
		if (to != null)
			params.put("to", to.toString());

		// Send request to Influx API
		HttpResponse<String> response =
				httpAPIs.sendRequest(influxURL + "/api/measurement/data", "GET", params, null, null);

		// Parse response with status code smaller than 300
		if (response != null && response.statusCode() < 300) {
			JSONArray data = new JSONArray(response.body());

			if (!data.isEmpty()) {
				data.forEach((obj) -> {
					if (obj instanceof JSONObject) {
						JSONObject json = ((JSONObject) obj).getJSONObject("fields");

						Long timestamp = DateConverter.toEpoch(json.getString("time"));
						if (ret.getUnit() != null) {
							ret.getData().put(timestamp, json.getDouble(ret.getUnit().getName()));
						} else {
							if (json.has("value")) {
								ret.getData().put(timestamp, json.getDouble("value"));
							}
						}
					}
				});
			}
		} else if (response != null) throw new HttpRuntimeException(String.format("Error retrieving data from Influx for Abtract meter %s with domain %s: %s", ret.getName(), domain.toString(), response.statusCode()));
		else throw new HttpRuntimeException(String.format("Error retrieving data from Influx for Abtract meter %s with domain %s: NULL response", ret.getName(), domain.toString()));

		return ret;
    }
	
	/**
	 * Returns an AbstractMeterDataDAO object after performing an
	 * Influx API request for aggregated data.
	 *
	 * @param  name     the name of the measurement
	 * @param  domain   the domain of the measurement
	 * @param  operation   the operation to perform on the measurement data
	 * @param  from     the starting timestamp of the data to retrieve (optional)
	 * @param  to       the ending timestamp of the data to retrieve (optional)
	 * @param  group    the group parameter to be sent to Influx API (optional)
	 * @return          an AbstractMeterDataDAO object containing the retrieved data
	 */
	public AbstractMeterDataDAO getAggregated(String name, Domain domain, InfluxFunction operation, Long from, Long to, String group) {
		
		AbstractMeterDataDAO ret = new AbstractMeterDataDAO();

		ret.setName(AbstractMeter.obtain(name));
		ret.setDomain(domain);

		Map<String, String> params = new HashMap<>();

		// Set parameters to Influx API request
		params.put("measurements", "abstract_meter");
		params.put("measurement_type", ret.getName().name().toLowerCase());
		params.put("domain", domain.name());
		if (from != null)
			params.put("from", from.toString());
		if (to != null)
			params.put("to", to.toString());
		if (group != null)
			params.put("group", group);

		// Send request to Influx API
		HttpResponse<String> response =
				httpAPIs.sendRequest(influxURL + "/api/measurement/data/" + operation.name().toLowerCase(), "GET", params, null, null);

		// Parse response with status code smaller than 300
		if (response != null && response.statusCode() < 300) {
			JSONArray data = new JSONArray(response.body());

			if (!data.isEmpty()) {
				data.forEach((obj) -> {
					if (obj instanceof JSONObject) {
						JSONObject json = ((JSONObject) obj).getJSONObject("fields");

						Long timestamp = null;
						try {
							if(json.has("time") && !json.isNull("time")) {
								timestamp = DateConverter.toEpoch(json.getString("time"));
							}
						} catch (InvalidArgumentException e) {
							log.warn(e.getMessage());
						}
						if (ret.getUnit() != null) {
							ret.getData().put(timestamp, json.getDouble(ret.getUnit().getName()));
						} else {
							if (json.has("value")) {
								ret.getData().put(timestamp, json.getDouble("value"));
							}
						}
					}
				});
			}
		} else if (response != null) throw new HttpRuntimeException(String.format("Error retrieving data from Influx for Abtract meter %s with domain %s: %s", ret.getName(), domain.toString(), response.statusCode()));
		else throw new HttpRuntimeException(String.format("Error retrieving data from Influx for Abtract meter %s with domain %s: NULL response", ret.getName(), domain.toString()));

		return ret;
    }
	
	public AbstractMeterDataDAO calculateAndInsert(String name, Domain domain, Long from, Long to, Long time) {
		Map<String, String> headers = Map.of("Content-Type", "application/json");
		
		AbstractMeterConfig meter = abstractMeterRepository.findByNameAndDomain(AbstractMeter.obtain(name), domain)
				.orElseThrow(() -> new NotFoundException("The abstract meter with name %s and domain %s isn't configured", name, domain));

		AbstractMeterDataDAO ret = AbstractMeterDataDAO.create(meter);
		MeasurementDAORequest influxRequest = MeasurementDAORequest.create(meter);
		
		if (time != null)
			influxRequest.getFields().put("time", DateConverter.toString(time));
		
		BigDecimal value = calculator.calculateFormula(meter.getFormula(), from, to);
		influxRequest.getFields().put("value", calculator.bigDecimalToDoubleString(value));
		
		HttpResponse<String> response = httpAPIs.sendRequest(influxURL + "/api/measurement", "POST", null, influxRequest, headers);
		
		if (response != null && response.statusCode() < 300) {
			ret.getData().put(Instant.now().getEpochSecond() * 1000, value.doubleValue());
		} else if (response != null) throw new HttpRuntimeException("Influx request failed with status code %d", response.statusCode());
		else throw new HttpRuntimeException("Influx request failed with NULL response");
		
		return ret;
	}
	
	public List<AbstractMeterDataDAO> calculateAndInsertAll(Long from, Long to, Long time) {
		Map<String, String> headers = Map.of("Content-Type", "application/json");
		
		List<AbstractMeterDataDAO> configuredMeters = new LinkedList<>();
		List<AbstractMeterConfig> meters = abstractMeterRepository.findAll();
		if (meters.isEmpty())
			throw new NotFoundException("There aren't abstract meters configured");

		meters.sort(
				(m1, m2) -> 
				m1.getDomain().equals(m2.getDomain())?
						0 : m1.getDomain().equals(Domain.electricity)?
								-1 : 1);
		
		for (AbstractMeterConfig meter : meters) {
			MeasurementDAORequest influxRequest = MeasurementDAORequest.create(meter);
			
			if (time != null)
				influxRequest.getFields().put("time", DateConverter.toString(time));

			BigDecimal value = new BigDecimal(0);
			if (meter.getCondition() == null || calculator.compare(meter.getCondition(), from, to))
				value = calculator.calculateFormula(meter.getFormula(), from, to);
				
			influxRequest.getFields().put("value", calculator.bigDecimalToDoubleString(value));
			
			HttpResponse<String> response = httpAPIs.sendRequest(influxURL + "/api/measurement", "POST", null, influxRequest, headers);
			
			if (response != null && response.statusCode() < 300) {
				AbstractMeterDataDAO data = AbstractMeterDataDAO.create(meter);
				data.getData().put(Instant.now().getEpochSecond() * 1000, value.doubleValue());
				configuredMeters.add(data);
			} else if (response != null) log.error(String.format("Error saving data in Influx for abstract meter %s with domain %s: %d", meter.getName().meter, meter.getDomain().toString(), response.statusCode()));
			else log.error(String.format("Error retrieving data from Influx for abstract meter %s with domain %s: NULL response", meter.getName().meter, meter.getDomain().toString()));
		}
		return configuredMeters;
	}
}
