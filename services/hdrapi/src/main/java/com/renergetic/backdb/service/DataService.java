package com.renergetic.backdb.service;

import com.renergetic.backdb.dao.DataDAO;
import com.renergetic.backdb.exception.NotFoundException;
import com.renergetic.backdb.model.InformationPanel;
import com.renergetic.backdb.model.InformationTile;
import com.renergetic.backdb.model.Measurement;
import com.renergetic.backdb.repository.InformationPanelRepository;
import com.renergetic.backdb.repository.InformationTileMeasurementRepository;
import com.renergetic.backdb.repository.InformationTileRepository;
import com.renergetic.backdb.repository.MeasurementRepository;
import com.renergetic.backdb.service.utils.HttpAPIs;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	InformationPanelRepository panelRepository;
	@Autowired
	InformationTileRepository tileRepository;
	@Autowired
	MeasurementRepository measurementRepository;
	
	@Autowired
	InformationTileMeasurementRepository tileMeasurementRepository;

	public DataDAO getByUserId(Long userId, Map<String, String> params){
		List<Measurement> measurements = measurementRepository.findByUserId(userId);

		DataDAO ret = new DataDAO();
		measurements.forEach(measurement -> {

			params.put("field", measurement.getType().getName());
			HttpResponse<String> responseLast = HttpAPIs.sendRequest(
					String.format("http://influx-api-swagger-ren-prototype.apps.paas-dev.psnc.pl/api/measurement/%s/last", measurement.getName()),
					"GET", params, null, null);
			
			HttpResponse<String> responseMax = HttpAPIs.sendRequest(
					String.format("http://influx-api-swagger-ren-prototype.apps.paas-dev.psnc.pl/api/measurement/%s/max", measurement.getName()),
					"GET", params, null, null);
			
			if (responseLast != null && responseLast.statusCode() < 300) {
				JSONArray array = new JSONArray(responseLast.body());
				if (array.length() > 0)
					ret.getCurrent().getLast().put(measurement.getId().toString(), Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("last")));
			} else System.err.println(responseMax.body());
			if (responseMax != null && responseMax.statusCode() < 300) {
				JSONArray array = new JSONArray(responseMax.body());
				if (array.length() > 0)
					ret.getCurrent().getMax().put(measurement.getId().toString(), Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("max")));
			} else System.err.println(responseMax.body());
		});
		return ret;
	}

	public DataDAO getByPanel(Long id, Map<String, String> params) {
		InformationPanel panel = panelRepository.findById(id).orElse(null);
		List<Measurement> measurements = new ArrayList<>();
		
		// GET MEASUREMENTS RELATED TO THE PANEL
		if (panel != null) {
			for (InformationTile tile : panel.getTiles()) {
				measurements.addAll(tileMeasurementRepository.findByInformationTileId(tile.getId()).stream().map(obj -> {
					if (obj.getMeasurement() != null)
						return obj.getMeasurement();
					else {
						Measurement ret = new Measurement();
						ret.setName(obj.getMeasurementName());
						ret.setType(obj.getType());
						ret.setSensorName(obj.getSensorName());
						ret.setDomain(obj.getDomain());
						ret.setDirection(obj.getDirection());
						
						return ret;
					}
				}).collect(Collectors.toList()));
			}
			DataDAO ret = new DataDAO();
			for (Measurement measurement : measurements) {
				if (!params.containsKey("field"))
					params.put("field", "value");
				// Execute request to Measurement API to get max and last values
				HttpResponse<String> responseLast = HttpAPIs.sendRequest(String.format("http://influx-api-swagger-ren-prototype.apps.paas-dev.psnc.pl/api/measurement/%s/last", measurement.getName()), "GET", params, null, null);
				HttpResponse<String> responseMax = HttpAPIs.sendRequest(String.format("http://influx-api-swagger-ren-prototype.apps.paas-dev.psnc.pl/api/measurement/%s/max", measurement.getName()), "GET", params, null, null);
				// If request are successfully executed format data
				if (responseLast.statusCode() < 300) {
					JSONArray array = new JSONArray(responseLast.body());
					if (array.length() > 0)
						ret.getCurrent().getLast().put(measurement.getId().toString(), Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("last")));
				}
				if (responseMax.statusCode() < 300) {
					JSONArray array = new JSONArray(responseMax.body());
					if (array.length() > 0)
						ret.getCurrent().getMax().put(measurement.getId().toString(), Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("max")));
				}
			}
			return ret;
		}
		else throw new NotFoundException("No panel found related with id " + id);
	}

	public DataDAO getByTile(Long id, Map<String, String> params) {
		InformationTile tile = tileRepository.findById(id).orElse(null);
		List<Measurement> measurements = new ArrayList<>();
		
		// GET MEASUREMENTS RELATED TO THE PANEL
		if (tile != null) {
			measurements.addAll(tileMeasurementRepository.findByInformationTileId(tile.getId()).stream().map(obj -> {
				if (obj.getMeasurement() != null)
					return obj.getMeasurement();
				else {
					Measurement ret = new Measurement();
					ret.setName(obj.getMeasurementName());
					ret.setType(obj.getType());
					ret.setSensorName(obj.getSensorName());
					ret.setDomain(obj.getDomain());
					ret.setDirection(obj.getDirection());
					
					return ret;
				}
			}).collect(Collectors.toList()));
			DataDAO ret = new DataDAO();
			for (Measurement measurement : measurements) {
				if (!params.containsKey("field"))
					params.put("field", "value");
				// Execute request to Measurement API to get max and last values
				HttpResponse<String> responseLast = HttpAPIs.sendRequest(String.format("http://influx-api-swagger-ren-prototype.apps.paas-dev.psnc.pl/api/measurement/%s/last", measurement.getName()), "GET", params, null, null);
				HttpResponse<String> responseMax = HttpAPIs.sendRequest(String.format("http://influx-api-swagger-ren-prototype.apps.paas-dev.psnc.pl/api/measurement/%s/max", measurement.getName()), "GET", params, null, null);
				// If request are successfully executed format data
				if (responseLast.statusCode() < 300) {
					JSONArray array = new JSONArray(responseLast.body());
					if (array.length() > 0)
						ret.getCurrent().getLast().put(measurement.getId().toString(), Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("last")));
				}
				if (responseMax.statusCode() < 300) {
					JSONArray array = new JSONArray(responseMax.body());
					if (array.length() > 0)
						ret.getCurrent().getMax().put(measurement.getId().toString(), Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("max")));
				}
			}
			return ret;
		}
		else throw new NotFoundException("No panel found related with id " + id);
	}
}
