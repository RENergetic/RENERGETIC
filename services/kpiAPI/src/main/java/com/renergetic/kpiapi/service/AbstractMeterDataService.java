package com.renergetic.kpiapi.service;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.renergetic.kpiapi.dao.AbstractMeterDataDAO;
import com.renergetic.kpiapi.exception.InvalidArgumentException;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.model.InfluxFunction;
import com.renergetic.kpiapi.model.MeasurementType;
import com.renergetic.kpiapi.repository.MeasurementTypeRepository;
import com.renergetic.kpiapi.service.utils.DateConverter;
import com.renergetic.kpiapi.service.utils.HttpAPIs;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AbstractMeterDataService {

	@Value("${influx.api.url}")
	private String influxURL;

	@Autowired
	private MeasurementTypeRepository measurementTypeRepository;
	
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

		ret.setName(name);
		ret.setDomain(domain);

		Map<String, String> params = new HashMap<>();

		// Set parameters to Influx API request
		params.put("measurements", name);
		params.put("domain", domain.name());
		if (from != null)
			params.put("from", from.toString());
		if (to != null)
			params.put("to", to.toString());

		// Send request to Influx API
		HttpResponse<String> response =
				HttpAPIs.sendRequest(influxURL + "/api/measurement/data", "GET", params, null, null);

		// Get measurement types
		List<MeasurementType> types = measurementTypeRepository.findAll();

		// Parse response with status code smaller than 300
		if (response.statusCode() < 300) {
			JSONArray data = new JSONArray(response.body());

			if (data.length() > 0) {
				data.forEach((obj) -> {
					if (obj instanceof JSONObject) {
						JSONObject json = ((JSONObject) obj).getJSONObject("fields");

						Long timestamp = DateConverter.toEpoch(json.getString("time"));
						if (ret.getUnit() != null) {
							ret.getData().put(timestamp, json.getDouble(ret.getUnit().getName()));
						} else {
							for (MeasurementType type : types) {
								if (json.has(type.getName())) {
									ret.getData().put(timestamp, json.getDouble(type.getName()));
									ret.setUnit(type);
									break;
								}
							}
						}
					}
				});
			}
		}

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

		ret.setName(name);
		ret.setDomain(domain);

		Map<String, String> params = new HashMap<>();

		// Set parameters to Influx API request
		params.put("measurements", name);
		params.put("domain", domain.name());
		if (from != null)
			params.put("from", from.toString());
		if (to != null)
			params.put("to", to.toString());
		if (group != null)
			params.put("group", group);

		// Send request to Influx API
		HttpResponse<String> response =
				HttpAPIs.sendRequest(influxURL + "/api/measurement/data/" + operation.name().toLowerCase(), "GET", params, null, null);

		// Get measurement types
		List<MeasurementType> types = measurementTypeRepository.findAll();

		// Parse response with status code smaller than 300
		if (response.statusCode() < 300) {
			JSONArray data = new JSONArray(response.body());

			if (data.length() > 0) {
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
							for (MeasurementType type : types) {
								if (json.has(type.getName())) {
									ret.getData().put(timestamp, json.getDouble(type.getName()));
									ret.setUnit(type);
									break;
								}
							}
						}
					}
				});
			}
		}

		return ret;
    }
}
