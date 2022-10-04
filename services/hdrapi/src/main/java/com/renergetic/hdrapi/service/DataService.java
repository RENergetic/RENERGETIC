package com.renergetic.hdrapi.service;

import com.renergetic.hdrapi.dao.DataDAO;
import com.renergetic.hdrapi.dao.DataWrapperDAO;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.Domain;
import com.renergetic.hdrapi.model.InformationPanel;
import com.renergetic.hdrapi.model.InformationTile;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.model.MeasurementType;
import com.renergetic.hdrapi.repository.AssetRepository;
import com.renergetic.hdrapi.repository.InformationPanelRepository;
import com.renergetic.hdrapi.repository.InformationTileMeasurementRepository;
import com.renergetic.hdrapi.repository.InformationTileRepository;
import com.renergetic.hdrapi.repository.MeasurementRepository;
import com.renergetic.hdrapi.repository.MeasurementTypeRepository;
import com.renergetic.hdrapi.service.utils.DummyDataGenerator;
import com.renergetic.hdrapi.service.utils.HttpAPIs;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DataService {
    @Value("${influx.api.url}")
    String influxURL;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    InformationPanelRepository panelRepository;
    @Autowired
    InformationTileRepository tileRepository;
    @Autowired
    AssetRepository assetRepository;
    @Autowired
    MeasurementRepository measurementRepository;
    @Autowired
    MeasurementTypeRepository measurementTypeRepository;

    @Autowired
    InformationTileMeasurementRepository tileMeasurementRepository;

    public DataDAO getByUserId(Long userId, Map<String, String> params) {
        List<Measurement> measurements = measurementRepository.findByUserId(userId);

        DataDAO ret = new DataDAO();
        measurements.forEach(measurement -> {

            params.put("field", measurement.getType().getName());
            HttpResponse<String> responseLast = HttpAPIs.sendRequest(
                    String.format(influxURL + "/api/measurement/%s/last", measurement.getName()),
                    "GET", params, null, null);

            HttpResponse<String> responseMax = HttpAPIs.sendRequest(
                    String.format(influxURL + "/api/measurement/%s/max", measurement.getName()),
                    "GET", params, null, null);

            if (responseLast != null && responseLast.statusCode() < 300) {
                JSONArray array = new JSONArray(responseLast.body());
                if (array.length() > 0)
                    ret.getCurrent().getLast().put(measurement.getId().toString(),
                            Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("last")));
            } else System.err.println(responseLast == null ? "URL not found" : responseLast.body());
            if (responseMax != null && responseMax.statusCode() < 300) {
                JSONArray array = new JSONArray(responseMax.body());
                if (array.length() > 0)
                    ret.getCurrent().getMax().put(measurement.getId().toString(),
                            Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("max")));
            } else System.err.println(responseMax == null ? "URL not found" : responseMax.body());
        });
        return ret;
    }

    //TODO: move panelservice?
    private List<Measurement> getPanelMeasurements(long id) {
        InformationPanel panel = panelRepository.findById(id).orElse(null);

        if (panel != null) {
            List<Measurement> measurements = new ArrayList<>();
            for (InformationTile tile : panel.getTiles()) {
                measurements.addAll(
                        tileMeasurementRepository.findByInformationTileId(tile.getId()).stream().map(obj -> {
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
            return measurements;
        }
        throw new NotFoundException("No panel found related with id " + id);

    }

    public DataDAO getByPanel(Long id, Map<String, String> params) {
//        InformationPanel panel = panelRepository.findById(id).orElse(null);
        List<Measurement> measurements = this.getPanelMeasurements(id);
        // GET MEASUREMENTS RELATED TO THE PANEL

        DataDAO ret = new DataDAO();
        for (Measurement measurement : measurements) {
            params.put("field", measurement.getType().getName());
            // Execute request to Measurement API to get max and last values
            HttpResponse<String> responseLast = HttpAPIs.sendRequest(
                    String.format(influxURL + "/api/measurement/%s/last", measurement.getName()), "GET", params,
                    null, null);
            HttpResponse<String> responseMax = HttpAPIs.sendRequest(
                    String.format(influxURL + "/api/measurement/%s/max", measurement.getName()), "GET", params,
                    null, null);
            // If request are successfully executed format data
            if (responseLast.statusCode() < 300) {
                JSONArray array = new JSONArray(responseLast.body());
                if (array.length() > 0)
                    ret.getCurrent().getLast().put(measurement.getId().toString(),
                            Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("last")));
            }
            if (responseMax.statusCode() < 300) {
                JSONArray array = new JSONArray(responseMax.body());
                if (array.length() > 0)
                    ret.getCurrent().getMax().put(measurement.getId().toString(),
                            Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("max")));
            }
        }
        return ret;
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
                params.put("field", measurement.getType().getName());
                // Execute request to Measurement API to get max and last values
                HttpResponse<String> responseLast = HttpAPIs.sendRequest(
                        String.format(influxURL + "/api/measurement/%s/last", measurement.getName()), "GET", params,
                        null, null);
                HttpResponse<String> responseMax = HttpAPIs.sendRequest(
                        String.format(influxURL + "/api/measurement/%s/max", measurement.getName()), "GET", params,
                        null, null);
                // If request are successfully executed format data
                if (responseLast.statusCode() < 300) {
                    JSONArray array = new JSONArray(responseLast.body());
                    if (array.length() > 0)
                        ret.getCurrent().getLast().put(measurement.getId().toString(),
                                Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("last")));
                }
                if (responseMax.statusCode() < 300) {
                    JSONArray array = new JSONArray(responseMax.body());
                    if (array.length() > 0)
                        ret.getCurrent().getMax().put(measurement.getId().toString(),
                                Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("max")));
                }
            }
            return ret;
        } else throw new NotFoundException("No tile found related with id " + id);
    }
    public DataWrapperDAO getPanelData(Long id,  Long from, Optional<Long> to) {
        return this.getPanelData(id,null,from,to );
    }

    public DataWrapperDAO getPanelData(Long id, Long assetId, Long from, Optional<Long> to) {
        //TODO: get measurements for assetId
        List<Measurement> measurements = this.getPanelMeasurements(id);

        // GET MEASUREMENTS RELATED TO THE PANEL

    	//TODO: build influxDB query
        DataDAO res = DummyDataGenerator.getData(measurements);
        return new DataWrapperDAO(res);

    }
    
    /**
     * Return energy production
     * @param bucket Influx bucket to search
     * @param from Date since search the production
     * @param to Date until search the production
     * @param domain To search production of determined domain (heat or electricity), set to null to search aññ
     * @return A map with renewables, fosil_fuels, external and total production
     */
	public HashMap<String, Double> getProduction(String bucket, Instant from, Instant to, Domain domain) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
		HashMap<String, String> params = new HashMap<>();
		HashMap<String, Double> ret = new HashMap<>();

    	ret.put("renewables", 0.);
    	ret.put("fosil_fuels", 0.);    
        ret.put("total", 0.);
		
		params.put("bucket", bucket);
		params.put("from", dateFormat.format(from));
		params.put("to", dateFormat.format(to));
		if (domain != null) params.put("domain", domain.toString());
		
        // Get consumption
		params.put("direction", "in");
        HttpResponse<String> responseConsumption = HttpAPIs.sendRequest(
                String.format("%s/api/measurement/data", influxURL), "GET", params,
                null, null);
        // If request are successfully executed format data
        if (responseConsumption.statusCode() < 300) {
        	List<MeasurementType> types = measurementTypeRepository.findAll().stream()
        			.filter(type -> type.getBaseUnit().equalsIgnoreCase("Wh"))
        			.collect(Collectors.toList());
        	JSONArray data = new JSONArray(responseConsumption.body());
        	
        	if (!data.isEmpty()) {
	        	for (Object obj : data) {
	        		JSONObject jsonObj = (JSONObject) obj;
	        		MeasurementType jsonType = types.stream().filter(type -> jsonObj.getJSONObject("fields").keySet().contains(type.getName()))
	        				.findFirst().orElse(null);
	        		ret.put("total", ret.get("total") + (jsonObj.getJSONObject("fields").optDouble(jsonType.getName(), 0) * jsonType.getFactor()));
	        	}
        	}
        }
        // Get production
		params.put("direction", "out");
        HttpResponse<String> responseProduction = HttpAPIs.sendRequest(
                String.format("%s/api/measurement/data", influxURL), "GET", params,
                null, null);
        // If request are successfully executed format data
        if (responseProduction.statusCode() < 300) {
        	JSONArray data = new JSONArray(responseProduction.body());
        	
        	if (!data.isEmpty()) {
        		List<Asset> assets = assetRepository.findAll();
	        	for (Object obj : data) {
	        		JSONObject jsonObj = (JSONObject) obj;
	        		String assetName = jsonObj.getJSONObject("tags").optString("asset_name", "");
	        		Asset asset = assets.stream().
	        				filter(elem -> elem.getName().equals(assetName)).
	        				findFirst().
	        				orElse(null);
	        		
	        		if (asset != null) {
	        			//System.err.println(asset.getName());
		        		double energy = jsonObj.getJSONObject("fields").optDouble("energy", 0);
		        		long renovability = asset.getType().getRenovable() != null? asset.getType().getRenovable() : 0;
	        			ret.put("renewables", 
	        					ret.get("renewables") + (energy * (renovability / 100)));
	        			ret.put("fosil_fuels", 
	        					ret.get("fosil_fuels") + (energy * ((100 - renovability) / 100)));
	        		} else System.err.println("Asset " + assetName + " not found");
	        	}
        	}
            ret.put("external", ret.get("total") - ret.get("renewables") - ret.get("fosil_fuels"));
        }
        return ret;
	}
}
