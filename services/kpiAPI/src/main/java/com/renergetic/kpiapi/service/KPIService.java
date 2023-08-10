package com.renergetic.kpiapi.service;

import com.renergetic.kpiapi.dao.AbstractMeterDataDAO;
import com.renergetic.kpiapi.dao.KPIDataDAO;
import com.renergetic.kpiapi.dao.MeasurementDAORequest;
import com.renergetic.kpiapi.exception.HttpRuntimeException;
import com.renergetic.kpiapi.exception.InvalidArgumentException;
import com.renergetic.kpiapi.exception.NotFoundException;
import com.renergetic.kpiapi.model.*;
import com.renergetic.kpiapi.repository.AbstractMeterRepository;
import com.renergetic.kpiapi.service.utils.DateConverter;
import com.renergetic.kpiapi.service.utils.HttpAPIs;
import com.renergetic.kpiapi.service.utils.MathCalculator;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KPIService {

	@Value("${influx.api.url}")
	private String influxURL;

	@Autowired
	private HttpAPIs httpAPIs;
	
	@Autowired
	private MathCalculator calculator;
	
	/**
	 * Retrieves the kpi data DAO for the given name, domain, and time range.
	 *
	 * @param  name   the name of the meter data
	 * @param  domain the domain of the meter data
	 * @param  from   the start time of the meter data (null for all time)
	 * @param  to     the end time of the meter data (null for all time)
	 * @return        the KPIDataDAO object containing the requested data
	 */
	public KPIDataDAO get(String name, Domain domain, Long from, Long to) {

		KPIDataDAO ret = new KPIDataDAO();

		ret.setName(KPI.obtain(name));
		ret.setDomain(domain);

		Map<String, String> params = new HashMap<>();

		// Set parameters to Influx API request
		params.put("measurements", ret.getName().name());
		params.put("domain", domain.name());
		if (from != null)
			params.put("from", from.toString());
		if (to != null)
			params.put("to", to.toString());

		// Send request to Influx API
		HttpResponse<String> response =
				httpAPIs.sendRequest(influxURL + "/api/measurement/data", "GET", params, null, null);

		// Parse response with status code smaller than 300
		if (response.statusCode() < 300) {
			JSONArray data = new JSONArray(response.body());

			if (data.isEmpty()) {
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
		}

		return ret;
    }
	
	/**
	 * Returns an KPIDataDAO object after performing an
	 * Influx API request for aggregated data.
	 *
	 * @param  name     the name of the measurement
	 * @param  domain   the domain of the measurement
	 * @param  operation   the operation to perform on the measurement data
	 * @param  from     the starting timestamp of the data to retrieve (optional)
	 * @param  to       the ending timestamp of the data to retrieve (optional)
	 * @param  group    the group parameter to be sent to Influx API (optional)
	 * @return          an KPIDataDAO object containing the retrieved data
	 */
	public KPIDataDAO getAggregated(String name, Domain domain, InfluxFunction operation, Long from, Long to, String group) {

		KPIDataDAO ret = new KPIDataDAO();

		ret.setName(KPI.obtain(name));
		ret.setDomain(domain);

		Map<String, String> params = new HashMap<>();

		// Set parameters to Influx API request
		params.put("measurements", ret.getName().name());
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
		if (response.statusCode() < 300) {
			JSONArray data = new JSONArray(response.body());

			if (data.isEmpty()) {
				data.forEach((obj) -> {
					if (obj instanceof JSONObject) {
						JSONObject json = ((JSONObject) obj).getJSONObject("fields");

						Long timestamp = null;
						try {
							timestamp = DateConverter.toEpoch(json.getString("time"));
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
		}

		return ret;
    }
	
	public List<KPIDataDAO> calculateAndInsertAll(Domain domain, Long from, Long to, Long time) {
		Map<String, String> headers = Map.of("Content-Type", "application/json");
		
		List<KPIDataDAO> configuredMeters = new LinkedList<>();
		
		for (KPI kpi : KPI.values()) {
			MeasurementDAORequest influxRequest = MeasurementDAORequest.create(kpi, domain);
			
			if (time != null)
				influxRequest.getFields().put("time", DateConverter.toString(time));

			BigDecimal value = null;

			// TODO: CALCULATE KPI VALUE
			
			HttpResponse<String> response = httpAPIs.sendRequest(influxURL + "/api/measurement", "POST", null, influxRequest, headers);
			
			if (response.statusCode() < 300) {
				KPIDataDAO data = KPIDataDAO.create(kpi, domain);
				data.getData().put(Instant.now().getEpochSecond() * 1000, 0.);// TODO: PUT INSERTED VALUE
				configuredMeters.add(data);
			} else log.error(String.format("Error saving data in Influx for KPI %s with domain %s: %s", kpi.description, domain.toString(), response.body()));
		}
		return configuredMeters;
	}
}
