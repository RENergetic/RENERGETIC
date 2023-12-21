package com.renergetic.wrapperapi.service;

import com.renergetic.wrapperapi.service.utils.DummyDataGenerator;

import com.renergetic.common.dao.*;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.*;
import com.renergetic.common.utilities.HttpAPIs;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private AssetCategoryRepository assetCategoryRepository;
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private MeasurementTagsRepository measurementTagsRepository;
    
    @Autowired
    private DummyDataGenerator dummyDataGenerator;
    @Autowired
    private HttpAPIs httpAPIs;

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
                                    .stream().map(Asset::getName).collect(Collectors.toList()));
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
}
