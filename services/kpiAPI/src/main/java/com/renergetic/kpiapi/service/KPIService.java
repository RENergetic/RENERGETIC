package com.renergetic.kpiapi.service;

import com.renergetic.kpiapi.dao.KPIDataDAO;
import com.renergetic.kpiapi.dao.MeasurementDAORequest;
import com.renergetic.kpiapi.exception.InvalidArgumentException;
import com.renergetic.kpiapi.model.*;
import com.renergetic.kpiapi.repository.KPIConstantRepository;
import com.renergetic.kpiapi.service.utils.DateConverter;
import com.renergetic.kpiapi.service.utils.HttpAPIs;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class KPIService {

	@Value("${influx.api.url}")
	private String influxURL;

	@Autowired
	private HttpAPIs httpAPIs;
	
	@Autowired
	private KPIConstantRepository constantRepository;
	
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
		params.put("measurements", "kpi");
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
		if (response.statusCode() < 300) {
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
		params.put("measurements", "kpi");
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
		if (response.statusCode() < 300) {
			JSONArray data = new JSONArray(response.body());

			if (!data.isEmpty()) {
				data.forEach(obj -> {
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

		// Prepare Abstract meter needed values
        Set<Thread> threads = new HashSet<>();
        
        Map<AbstractMeter, Double> values = new EnumMap<>(AbstractMeter.class);
        Map<AbstractMeter, Double> previousValues = new EnumMap<>(AbstractMeter.class);
        Map<AbstractMeter, Double> maxValues = new EnumMap<>(AbstractMeter.class);
        
        values.put(AbstractMeter.LOAD, 0.);
        values.put(AbstractMeter.LOSSES, 0.);
        values.put(AbstractMeter.STORAGE, 0.);
        values.put(AbstractMeter.ENS, 0.);
        values.put(AbstractMeter.ERS, 0.);
        values.put(AbstractMeter.EXCESS, 0.);
        values.put(AbstractMeter.RES, 0.);
        values.put(AbstractMeter.LNS, 0.);
        values.put(AbstractMeter.LRS, 0.);

        previousValues.put(AbstractMeter.LOAD, 0.); 
        previousValues.put(AbstractMeter.LOSSES, 0.); 
        previousValues.put(AbstractMeter.STORAGE, 0.); 

        maxValues.put(AbstractMeter.LOAD, 0.);
        maxValues.put(AbstractMeter.LOSSES, 0.);
        maxValues.put(AbstractMeter.STORAGE, 0.);

        values.forEach((key, value) -> 
        	threads.add(new Thread(() -> values.put(key, this.getAbstractMeterData(key, domain, from, to, InfluxFunction.SUM))))
    	);

        previousValues.forEach((key, value) -> 
        	threads.add(new Thread(() -> previousValues.put(key, this.getAbstractMeterData(key, domain, from - (to - from), from, InfluxFunction.SUM))))
    	);

        maxValues.forEach((key, value) -> 
        	threads.add(new Thread(() -> maxValues.put(key, this.getAbstractMeterData(key, domain, from, to, InfluxFunction.MAX))))
    	);
        
        threads.forEach(Thread::start);
		
		// Wait to retrieve all data
		threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
				thread.interrupt();
                e.printStackTrace();
            }
        });
		
		Map<String, String> headers = Map.of("Content-Type", "application/json");
		
		List<KPIDataDAO> configuredMeters = new LinkedList<>();
		
		// Calculate and save each KPI
		for (KPI kpi : KPI.values()) {
			MeasurementDAORequest influxRequest = MeasurementDAORequest.create(kpi, domain);
			
			if (time != null)
				influxRequest.getFields().put("time", DateConverter.toString(time));

			Double value = calculateKPI(kpi, domain, from, to, values, previousValues, maxValues).doubleValue();
			if (!Double.isNaN(value))
				influxRequest.getFields().put("value", String.valueOf(value.doubleValue()));
			else influxRequest.getFields().put("value", "0.0");

			influxRequest.getFields().put("value", String.valueOf(value.doubleValue()));
			
			HttpResponse<String> response = httpAPIs.sendRequest(influxURL + "/api/measurement", "POST", null, influxRequest, headers);
			
			if (response.statusCode() < 300) {
				KPIDataDAO data = KPIDataDAO.create(kpi, domain);
				data.getData().put(Instant.now().getEpochSecond() * 1000, value.doubleValue());
				configuredMeters.add(data);
			} else log.error(String.format("Error saving data in Influx for KPI %s with domain %s: %s", kpi.description, domain.toString(), response.statusCode()));
		}
		return configuredMeters;
	}
	
	public BigDecimal calculateKPI(KPI kpi, Domain domain, Long from, Long to, Map<AbstractMeter, Double> values, Map<AbstractMeter, Double> previousValues, Map<AbstractMeter, Double> maxValues) {
		
		// Calculate each KPI with the values retrieved before
		return switch (kpi) {
			case ESS -> this.calculateESS(values);
			case EP -> this.calculateEP(values);
			case EE -> this.calculateEE(values);
			case ES -> this.calculateES(values, previousValues);
			case SRES -> this.calculateSRES(values);
			case SNES -> this.calculateSNES(values);
			case CO2 -> this.calculateCO2(values);
			case PEAK -> this.calculatePEAK(maxValues);
		};
	}
	
	public BigDecimal calculateESS(Map<AbstractMeter, Double> values) {
		
		Double result = (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE) - 
				(values.get(AbstractMeter.ENS) + values.get(AbstractMeter.ERS))) / 
				(values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));
		
		if (!Double.isNaN(result))
			return BigDecimal.valueOf(result);
		else return new BigDecimal(0);
	}
	
	public BigDecimal calculateEP(Map<AbstractMeter, Double> values) {
		
		Double result = (values.get(AbstractMeter.EXCESS) + values.get(AbstractMeter.LOSSES) + 
				(values.get(AbstractMeter.ENS) + values.get(AbstractMeter.ERS))) / 
				(values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));

		if (!Double.isNaN(result))
			return BigDecimal.valueOf(result);
		else return new BigDecimal(0);
	}
	
	public BigDecimal calculateEE(Map<AbstractMeter, Double> values) {
		
		Double result = 1 - (values.get(AbstractMeter.LOSSES) / 
				(values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE)));

		if (!Double.isNaN(result))
			return BigDecimal.valueOf(result);
		else return new BigDecimal(0);
	}
	
	public BigDecimal calculateES(Map<AbstractMeter, Double> values, Map<AbstractMeter, Double> previousValues) {
		
		Double result = ((previousValues.get(AbstractMeter.LOAD) + previousValues.get(AbstractMeter.LOSSES) + previousValues.get(AbstractMeter.STORAGE)) - 
				(values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE))) / 
				(values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));

		if (!Double.isNaN(result))
			return BigDecimal.valueOf(result);
		else return new BigDecimal(0);
	}
	
	public BigDecimal calculateSRES(Map<AbstractMeter, Double> values) {
		
		Double result = (values.get(AbstractMeter.LRS) + values.get(AbstractMeter.ERS) + values.get(AbstractMeter.RES)) / 
				(values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES));

		if (!Double.isNaN(result))
			return BigDecimal.valueOf(result);
		else return new BigDecimal(0);
	}
	
	public BigDecimal calculateSNES(Map<AbstractMeter, Double> values) {
		
		Double result = 1 - ((values.get(AbstractMeter.LRS) + values.get(AbstractMeter.ERS) + values.get(AbstractMeter.RES)) / 
				(values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES)));

		if (!Double.isNaN(result))
			return BigDecimal.valueOf(result);
		else return new BigDecimal(0);
	}
	
	public BigDecimal calculateCO2(Map<AbstractMeter, Double> values) {
		KPIConstant c = constantRepository.findAll().stream().findFirst().orElse(new KPIConstant(1L, 1., 1., 1., 1.));
		
		log.debug(String.format("Constants: a -> %.2f | b -> %.2f | g -> %.2f | d -> %.2f", c.getAlpha(), c.getBeta(), c.getGamma(), c.getDelta()));
		
		Double result = ((c.getAlpha() * values.get(AbstractMeter.LRS) + c.getBeta() * values.get(AbstractMeter.ERS) + 
				c.getGamma() * values.get(AbstractMeter.ENS) + c.getDelta() * values.get(AbstractMeter.LNS))) / 
				(values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));

		if (!Double.isNaN(result))
			return BigDecimal.valueOf(result);
		else return new BigDecimal(0);
	}
	
	public BigDecimal calculatePEAK(Map<AbstractMeter, Double> values) {
		
		Double result = values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE);

		if (!Double.isNaN(result))
			return BigDecimal.valueOf(result);
		else return new BigDecimal(0);
	}
	
	private Double getAbstractMeterData(AbstractMeter meter, Domain domain, Long from, Long to, InfluxFunction operation) {
		Map<String, String> params = new HashMap<>();

		// Set parameters to Influx API request
		params.put("measurements", "abstract_meter");
		params.put("measurement_type", meter.name().toLowerCase());
		params.put("domain", domain.name());
		if (from != null)
			params.put("from", from.toString());
		if (to != null)
			params.put("to", to.toString());

		// Send request to Influx API
		HttpResponse<String> response =
				httpAPIs.sendRequest(influxURL + "/api/measurement/data/" + operation.name().toLowerCase(), "GET", params, null, null);

		// Parse response with status code smaller than 300
		if (response.statusCode() < 300) {
			JSONArray data = new JSONArray(response.body());

			if (!data.isEmpty()) {
				for (Object obj : data) {
					if (obj instanceof JSONObject) {
						JSONObject json = ((JSONObject) obj).getJSONObject("fields");

						if (json.has(operation.name().toLowerCase())) {
							return json.getDouble(operation.name().toLowerCase());
						}
					}
				}
			}
		}
		return 0.;
	}
}
