package com.renergetic.ruleevaluationservice.service;

import com.google.gson.Gson;
import com.renergetic.common.model.Details;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.MeasurementTagsRepository;
import com.renergetic.ruleevaluationservice.dao.MeasurementSimplifiedDAO;
import com.renergetic.ruleevaluationservice.service.utils.HttpAPIs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataService {
    @Value("${influx.api.url}")
    String influxURL;

    List<String> cumulativeTypes = List.of();

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private MeasurementTagsRepository measurementTagsRepository;
    @Autowired
    private HttpAPIs httpAPIs;

    private final Gson gson = new Gson();

    public List<MeasurementSimplifiedDAO> getData(Measurement measurement, String function, String group, Long from, Optional<Long> to) {
        List<Measurement> measurements = new ArrayList<>();
        measurements.add(measurement);
        return getData(measurements, function, group, from, to);
    }

    public List<MeasurementSimplifiedDAO> getData(List<Measurement> measurements, String function, String group, Long from, Optional<Long> to) {
        /*
            TODO: Check if all measurements are compatible !
         */

        /*
            TODO: Instead of doing on the fly aggregation, can store in influx, with overlap in time
            Insert should replace the existing time in the timeserie
         */

        Measurement refMeasurement = measurements.get(0);

        Map<String, String> params = new HashMap<>();
        List<String> assetNames = new LinkedList<>();
        //TODO: Could use the function from aggregator measurement
        function = function != null ? function : "last";

        // GET ASSETS RELATED WITH THE MEASUREMENT (If the assets is a energy island and there isn't category it doesn't filter by asset)

        for(Measurement measurement : measurements){
            if (measurement.getAsset() != null && measurement.getAsset().getType() != null &&
                    !measurement.getAsset().getType().getName().equalsIgnoreCase("energy_island"))
                assetNames.add(measurement.getAsset().getName());
            //TODO: Here there is a difference with Raúl's implementation, is it still needed the asset category ?
        }

        // GET MEASUREMENT TAGS
        List<MeasurementTags> tags = measurementTagsRepository.findByMeasurementId(refMeasurement.getId());

        // PREPARE INFLUX FILTERS
        if (from != null)
            params.put("from", from.toString());
        if (to != null && to.isPresent())
            params.put("to", to.get().toString());
        if (refMeasurement.getName() != null)
            params.put("measurement_type", refMeasurement.getName());
        if (refMeasurement.getSensorName() != null)
            params.put("measurements", refMeasurement.getSensorName());
        if (refMeasurement.getType() != null)
            params.put("fields", refMeasurement.getType().getName());
        if (refMeasurement.getDirection() != null)
            params.put("direction", refMeasurement.getDirection().name());
        if (refMeasurement.getDomain() != null)
            params.put("domain", refMeasurement.getDomain().name());
        if (refMeasurement.getSensorId() != null)
            params.put("sensor_id", refMeasurement.getSensorId());
        if (!assetNames.isEmpty())
            params.put("asset_name", String.join(",", assetNames));
        if (tags != null && !tags.isEmpty())
            params.putAll(tags.stream()
                    .filter(tag -> !params.containsKey(tag.getValue()))
                    .collect(Collectors.toMap(Details::getKey, Details::getValue)));

        // PARSE TO NON CUMULATIVE DATA IF THE MEASUREMENT IS CUMULATIVE
        MeasurementDetails cumulative = null;
        if (refMeasurement.getDetails() != null)
            cumulative = refMeasurement.getDetails().stream().filter(
                    details -> details.getKey().equalsIgnoreCase("cumulative")).findFirst().orElse(null);

        if (cumulative == null && cumulativeTypes.contains(refMeasurement.getType().getPhysicalName())) {
            params.put("performDecumulation", "true");
        } else if (cumulative != null) {
            params.put("performDecumulation", cumulative.getValue());
        }

        if(group != null)
            params.put("group", group);

        // INFLUX API REQUEST
        HttpResponse<String> response =
                httpAPIs.sendRequest(influxURL + "/api/measurement/data/" + function, "GET", params, null,
                        null);

        if (response.statusCode() < 300) {
            return List.of(gson.fromJson(response.body(), MeasurementSimplifiedDAO[].class));
        }
        return new ArrayList<>();
    }
}
