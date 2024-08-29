package com.renergetic.dataapi.service;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.renergetic.common.dao.DataDAO;
import com.renergetic.common.dao.DataWrapperDAO;
import com.renergetic.common.dao.InformationPanelDAOResponse;
import com.renergetic.common.dao.InformationTileDAOResponse;
import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.dao.TimeseriesDAO;
import com.renergetic.common.model.AssetCategory;
import com.renergetic.common.model.Details;
import com.renergetic.common.model.InformationTileMeasurement;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.AssetCategoryRepository;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.common.repository.MeasurementTagsRepository;
import com.renergetic.common.repository.MeasurementTypeRepository;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.common.utilities.HttpAPIs;
import com.renergetic.dataapi.service.utils.Basic;
import com.renergetic.dataapi.service.utils.DummyDataGenerator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;



@Service
public class DataService {
    @Value("${influx.api.url}")
    String influxURL;
    @Value("${api.generate.dummy-data}")
    private boolean generateDummy;
    @PersistenceContext
    EntityManager entityManager;

    List<String> cumulativeTypes = List.of();

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    AssetCategoryRepository assetCategoryRepository;
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private MeasurementTagsRepository measurementTagsRepository;
    @Autowired
    private MeasurementTypeRepository measurementTypeRepository;
    @Autowired
    private InformationPanelService informationPanelService;

    @Autowired
    HttpAPIs httpAPIs;
    @Autowired
    private DummyDataGenerator dummyDataGenerator;

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
                            Collectors.toMap(m -> m.getFunction() + "_" + m.getId(), Function.identity(),
                                    (m1, m2) -> m1)).values();
            DataDAO res = this.getData(values, from, to);

            return new DataWrapperDAO(res, assetTemplate);
        } else {
            List<InformationTileMeasurement> measurements =
                    informationPanelService.getPanelMeasurements(panelId);
            List<MeasurementDAOResponse> measurementDAOResponseList = measurements.stream()
                .filter(it->it.getMeasurement()!=null)
                .map(it ->
                    MeasurementDAOResponse.create(it.getMeasurement(), it.getMeasurement().getDetails(),
                            it.getFunction())).collect(Collectors.toList());
            DataDAO res = this.getData(measurementDAOResponseList, from, to);
            return new DataWrapperDAO(res);
        }

    }

    public TimeseriesDAO getTileTimeseries(Long tileId, Long assetId, Long from, Optional<Long> to) {
        List<Measurement> measurements = informationPanelService.getTileMeasurements(tileId, assetId, null);
        return this.getTimeseries(measurements, from, to);
    }

    public TimeseriesDAO getMeasurementTimeseries(List<Long> measurementIds, Long from, Optional<Long> to) {
        List<Measurement> measurements = measurementRepository.findByIds(measurementIds);

        return this.getTimeseries(measurements, from, to);
    }

    public DataDAO getData(Collection<MeasurementDAOResponse> measurements, Long from, Optional<Long> to) {
        if (generateDummy) {
            return dummyDataGenerator.getData(measurements);
        } else {
            DataDAO ret = new DataDAO();
            Set<Thread> threads = new HashSet<>();

            for (final MeasurementDAOResponse measurement : measurements) {
                Thread thread = new Thread(() -> {
                    Measurement m = measurementRepository.findById(measurement.getId()).get();
                    Map<String, String> params = new HashMap<>();
                    List<String> assetNames = new LinkedList<>();
                    String function = measurement.getFunction() != null ? measurement.getFunction().name() : "last";

                    // SET DEFAULT VALUES TO THE MEASUREMENT
                    ret.putCurrent(function, measurement.getId().toString(), null);

                    // GET ASSETS RELATED WITH THE MEASUREMENT (If the assets is a energy island and there isn't category it doesn't filter by asset)

                    if (measurement.getAsset() != null && measurement.getAsset().getType() != null &&
                            !measurement.getAsset().getType().getName().equalsIgnoreCase("energy_island"))
                        assetNames.add(measurement.getAsset().getName());
                    if (measurement.getCategory() != null) {
                        Optional<AssetCategory> first = assetCategoryRepository.findByNameContaining(
                                measurement.getCategory()).stream().findFirst();
                        if (first.isPresent())
                            assetNames.addAll(assetRepository.findByAssetCategoryId(first.get().getId())
                                    .stream().map(asset -> asset.getName()).collect(Collectors.toList()));
                    }
                    // GET MEASUREMENT TAGS
                    List<MeasurementTags> tags = measurementTagsRepository.findByMeasurementId(measurement.getId());

                    // PREPARE INFLUX FILTERS
                    if (from != null)
                        params.put("from", from.toString());
                    if (to != null && to.isPresent())
                        params.put("to", to.get().toString());
                    if (measurement.getName() != null)
                        params.put("measurement_type", measurement.getName());
                    if (measurement.getSensorName() != null)
                        params.put("measurements", measurement.getSensorName());
                    if (measurement.getType() != null)
                        params.put("fields", measurement.getType().getName());
                    if (measurement.getDirection() != null)
                        params.put("direction", measurement.getDirection().name());
                    if (measurement.getDomain() != null)
                        params.put("domain", measurement.getDomain().name());
                    if (m.getSensorId() != null)
                        params.put("sensor_id", m.getSensorId());
                    if (assetNames != null && !assetNames.isEmpty())
                        params.put("asset_name", assetNames.stream().collect(Collectors.joining(",")));
                    if (tags != null && !tags.isEmpty())
                        params.putAll(tags.stream()
                                .filter(tag -> !params.containsKey(tag.getValue()))
                                .collect(Collectors.toMap(Details::getKey, Details::getValue)));

                    // PARSE TO NON CUMULATIVE DATA IF THE MEASUREMENT IS CUMULATIVE
                    String cumulative = null;

                    if (measurement.getMeasurementDetails() != null && measurement.getMeasurementDetails().containsKey("cumulative"))
                        cumulative = measurement.getMeasurementDetails().get("cumulative").toString();

                    if (cumulative == null && cumulativeTypes.contains(measurement.getType().getPhysicalName())) {
                        params.put("performDecumulation", "true");
                    } else if (cumulative != null) {
                        params.put("performDecumulation", cumulative);
                    }

                    // INFLUX API REQUEST
                    HttpResponse<String> response =
                            httpAPIs.sendRequest(influxURL + "/api/measurement/data/" + function, "GET", params, null,
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
                    measurements.stream().map(m -> MeasurementDAOResponse.create(m, null, null)).collect(
                            Collectors.toList()),
                    from, to);
        } else {
            Map<String, JSONArray> responses = new HashMap<>();
            TimeseriesDAO ret = new TimeseriesDAO();
            Set<Thread> threads = new HashSet<>();

            List<String> types = measurementTypeRepository.findAll().stream().map(MeasurementType::getName).collect(
                    Collectors.toList());

            for (final Measurement measurement : measurements) {
                Thread thread = new Thread(() -> {
                    Map<String, String> params = new HashMap<>();
                    List<String> assetNames = new LinkedList<>();

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
                    if (measurement.getName() != null)
                        params.put("measurement_type", measurement.getName());
                    if (measurement.getSensorName() != null)
                        params.put("measurements", measurement.getSensorName());
                    if (measurement.getType() != null)
                        params.put("fields", measurement.getType().getName());
                    if (measurement.getDirection() != null)
                        params.put("direction", measurement.getDirection().name());
                    if (measurement.getDomain() != null)
                        params.put("domain", measurement.getDomain().name());
                    if (measurement.getSensorId() != null)
                        params.put("sensor_id", measurement.getSensorId());
                    if (assetNames != null && !assetNames.isEmpty())
                        params.put("asset_name", assetNames.stream().collect(Collectors.joining(",")));
                    if (tags != null && !tags.isEmpty())
                        params.putAll(tags.stream()
                                .filter(tag -> !params.containsKey(tag.getValue()))
                                .collect(Collectors.toMap(tag -> tag.getKey(), tag -> tag.getValue())));

                    // INFLUX API REQUEST
                    HttpResponse<String> response =
                            httpAPIs.sendRequest(influxURL + "/api/measurement/data", "GET", params, null, null);

                    if (response.statusCode() < 300) {
                        responses.put(measurement.getId().toString(), new JSONArray(response.body()));
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

            Map<Long, Map<String, Double>> formattedResponse = new TreeMap<>();
            responses.forEach((measurementId, response) -> {
                if (response.length() > 0) {
                    response.forEach(obj -> {
                        if (obj instanceof JSONObject) {
                            JSONObject json = ((JSONObject) obj).getJSONObject("fields");

                            for (String type : types) {
                                if (json.has(type)) {
                                    Long timestamp = DateConverter.toEpoch(json.getString("time"));

                                    if (!formattedResponse.containsKey(timestamp)) {
                                        formattedResponse.put(timestamp, new TreeMap<>());

                                        measurements.forEach(measurement -> {
                                            if (measurementId.equals(measurement.getId().toString()))
                                                formattedResponse.get(timestamp).put(measurementId,
                                                        json.getDouble(type));
                                            else formattedResponse.get(timestamp).put(measurement.getId().toString(),
                                                    null);
                                        });
                                    } else {
                                        formattedResponse.get(timestamp).put(measurementId, json.getDouble(type));
                                    }
                                }
                            }
                        }
                    });
                }
            });
            ret.setTimestamps(new ArrayList<>(formattedResponse.keySet()));
            ret.setCurrent(Basic.combineMaps(formattedResponse.values()));
            return ret;
        }
    }
}
