package com.renergetic.hdrapi.service;

import com.renergetic.hdrapi.dao.*;
import com.renergetic.hdrapi.model.*;
import com.renergetic.hdrapi.model.details.MeasurementTags;
import com.renergetic.hdrapi.repository.*;
import com.renergetic.hdrapi.service.utils.DateConverter;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataService {
    @Value("${influx.api.url}")
    String influxURL;
    @Value("${api.generate.dummy-data}")
    private Boolean generateDummy;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private DummyDataGenerator dummyDataGenerator;
    @Autowired
    private MeasurementTagsRepository measurementTagsRepository;
    @Autowired
    private InformationPanelService informationPanelService;
    @Autowired
    private MeasurementTypeRepository measurementTypeRepository;

    public DataWrapperDAO getPanelData(Long panelId, Long from, Optional<Long> to) {
        return this.getPanelData(panelId, null, from, to);
    }

    /**
     * get data for the panel template
     *
     * @param panelId
     * @param assetId
     * @param from
     * @param to
     * @return
     */
    public DataWrapperDAO getPanelData(Long panelId, Long assetId, Long from, Optional<Long> to) {
        if (assetId != null) {
            InformationPanelDAOResponse assetTemplate =
                    informationPanelService.getAssetTemplate(panelId, assetId);

            Stream<MeasurementDAOResponse> measurementDAOResponseStream = assetTemplate.getTiles().stream().map(
                    InformationTileDAOResponse::getMeasurements
            ).flatMap(List::stream);
            Collection<MeasurementDAOResponse> values =
                    measurementDAOResponseStream.collect(
                            Collectors.toMap(MeasurementDAOResponse::getId, Function.identity(),
                                    (m1, m2) -> m1)).values();
            DataDAO res =
                    this.getData(values.stream().map(v -> v.mapToEntity()).collect(Collectors.toList()), from, to);
            //we need to return template with filled measurements for the given assetId
            return new DataWrapperDAO(res, assetTemplate);
        } else {
            //TODO: TOMEK/or someone else - check : if asset is null and panel is an template - raise exception  - bad request
            List<Measurement> measurements =
                    informationPanelService.getPanelMeasurements(panelId);
            DataDAO res = this.getData(measurements, from, to);
            return new DataWrapperDAO(res);
        }

    }

    public TimeseriesDAO getTileTimeseries(  Long tileId, Long assetId, Long from, Optional<Long> to) {
        List<Measurement> measurements = informationPanelService.getTileMeasurements(  tileId, assetId,null);
        TimeseriesDAO res = this.getTimeseries(measurements, from, to);
        return res;


    }

    public DataDAO getData(Collection<Measurement> measurements, Long from, Optional<Long> to) {
        if (generateDummy) {
            return dummyDataGenerator.getData(
                    measurements.stream().map(m -> MeasurementDAOResponse.create(m, null)).collect(
                            Collectors.toList()));
        } else {
            DataDAO ret = new DataDAO();
            Set<Thread> threads = new HashSet<>();

            for (final Measurement measurement : measurements) {
                Thread thread = new Thread(() -> {
                    Map<String, String> params = new HashMap<>();
                    List<String> assetNames = new LinkedList<>();
                    String function = measurement.getFunction() != null ? measurement.getFunction() : "last";

                    // SET DEFAULT VALUES TO THE MEASUREMENT
                    ret.putCurrent(function, measurement.getId().toString(), null);

                    // GET ASSETS RELATED WITH THE MEASUREMENT (If the assets is a energy island and there isn't category it doesn't filter by asset)
                    if (measurement.getAsset() != null && !measurement.getAsset().getType().getName().equalsIgnoreCase(
                            "energy_island"))
                        assetNames.add(measurement.getAsset().getName());
                    if (measurement.getAssetCategory() != null)
                        assetNames.addAll(assetRepository.findByAssetCategoryId(measurement.getAssetCategory().getId())
                                .stream().map(asset -> asset.getName()).collect(Collectors.toList()));

                    // GET MEASUREMENT TAGS
                    List<MeasurementTags> tags = measurementTagsRepository.findByMeasurementId(measurement.getId());

                    // PREPARE INFLUX FILTERS
                    if (from != null)
                        params.put("from", from.toString());
                    if (to != null && to.isPresent())
                        params.put("to", to.get().toString());
                    if (measurement.getSensorName() != null)
                        params.put("measurements", measurement.getSensorName());
                    if (measurement.getType() != null)
                        params.put("fields", measurement.getType().getName());
                    if (measurement.getDirection() != null)
                        params.put("direction", measurement.getDirection().name());
                    if (measurement.getDomain() != null)
                        params.put("domain", measurement.getDomain().name());
                    if (assetNames != null && !assetNames.isEmpty())
                        params.put("asset_name", assetNames.stream().collect(Collectors.joining(",")));
                    if (tags != null && !tags.isEmpty())
                        params.putAll(tags.stream()
                                .filter(tag -> !params.containsKey(tag.getValue()))
                                .collect(Collectors.toMap(tag -> tag.getKey(), tag -> tag.getValue())));

                    // INFLUX API REQUEST
                    HttpResponse<String> response =
                            HttpAPIs.sendRequest(influxURL + "/api/measurement/data/" + function, "GET", params, null,
                                    null);

                    if (response.statusCode() < 300) {
                        JSONArray array = new JSONArray(response.body());
                        if (array.length() > 0)
                            array.forEach(obj -> {
                                if (obj instanceof JSONObject) {
                                    JSONObject json = (JSONObject) obj;
                                    if (json.has("measurement"))
                                        ret.putCurrent(function
                                                , measurement.getId().toString()
                                                , Double.parseDouble(json.getJSONObject("fields").getString(function)));
                                }
                            });
                    }
                });
                thread.start();
                threads.add(thread);
            }
            threads.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            return ret;
        }
    }

    public TimeseriesDAO getTimeseries(Collection<Measurement> measurements, Long from, Optional<Long> to) {
        if (generateDummy) {
            return dummyDataGenerator.getTimeseries(
                    measurements.stream().map(m -> MeasurementDAOResponse.create(m, null)).collect(Collectors.toList()),
                    from, to);
        } else {
            TimeseriesDAO ret = new TimeseriesDAO();
            Set<Thread> threads = new HashSet<>();
            
            List<String> types = measurementTypeRepository.findAll().stream().map(MeasurementType::getName).collect(Collectors.toList());
            
            for (final Measurement measurement : measurements) {
                Thread thread = new Thread(() -> {
                    Map<String, String> params = new HashMap<>();
                    List<String> assetNames = new LinkedList<>();

                    // SET DEFAULT VALUES TO THE MEASUREMENT
                    ret.getCurrent().put(measurement.getId().toString(), new ArrayList<>());

                    // GET ASSETS RELATED WITH THE MEASUREMENT (If the assets is a energy island and there isn't category it doesn't filter by asset)
                    if (measurement.getAsset() != null && !measurement.getAsset().getType().getName().equalsIgnoreCase(
                            "energy_island"))
                        assetNames.add(measurement.getAsset().getName());
                    if (measurement.getAssetCategory() != null)
                        assetNames.addAll(assetRepository.findByAssetCategoryId(measurement.getAssetCategory().getId())
                                .stream().map(asset -> asset.getName()).collect(Collectors.toList()));

                    // GET MEASUREMENT TAGS
                    List<MeasurementTags> tags = measurementTagsRepository.findByMeasurementId(measurement.getId());

                    // PREPARE INFLUX FILTERS
                    if (from != null)
                        params.put("from", from.toString());
                    if (to != null && to.isPresent())
                        params.put("to", to.get().toString());
                    if (measurement.getSensorName() != null)
                        params.put("measurements", measurement.getSensorName());
                    if (measurement.getType() != null)
                        params.put("fields", measurement.getType().getName());
                    if (measurement.getDirection() != null)
                        params.put("direction", measurement.getDirection().name());
                    if (measurement.getDomain() != null)
                        params.put("domain", measurement.getDomain().name());
                    if (assetNames != null && !assetNames.isEmpty())
                        params.put("asset_name", assetNames.stream().collect(Collectors.joining(",")));
                    if (tags != null && !tags.isEmpty())
                        params.putAll(tags.stream()
                                .filter(tag -> !params.containsKey(tag.getValue()))
                                .collect(Collectors.toMap(tag -> tag.getKey(), tag -> tag.getValue())));

                    // INFLUX API REQUEST
                    HttpResponse<String> response =
                            HttpAPIs.sendRequest(influxURL + "/api/measurement/data", "GET", params, null, null);

                    if (response.statusCode() < 300) {
                        JSONArray array = new JSONArray(response.body());
                        if (array.length() > 0)
                            array.forEach(obj -> {
                                if (obj instanceof JSONObject) {
                                    JSONObject json = ((JSONObject) obj).getJSONObject("fields");
                                    for (String type : types) {
                                    	if (json.has(type)) {
                                    		ret.getTimestamps().add(DateConverter.toEpoch(json.getString("time")));
                                    		ret.getCurrent().get(measurement.getId().toString()).add(json.getDouble(type));
                                    		break;
                                    	}
                                    }
                                }
                            });
                    }
                });
                thread.start();
                threads.add(thread);
            }
            threads.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            return ret;
        }
    }
}
