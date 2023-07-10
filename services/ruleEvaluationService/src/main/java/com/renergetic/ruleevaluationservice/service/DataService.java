package com.renergetic.ruleevaluationservice.service;

import com.renergetic.common.model.Details;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.MeasurementTagsRepository;
import com.renergetic.ruleevaluationservice.dao.DataFields;
import com.renergetic.ruleevaluationservice.dao.DataResponse;
import com.renergetic.ruleevaluationservice.service.utils.HttpAPIs;
import org.json.JSONArray;
import org.json.JSONObject;
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

    public DataResponse getData(Measurement measurement, String function, String group, Long from, Optional<Long> to) {
        Map<String, String> params = new HashMap<>();
        List<String> assetNames = new LinkedList<>();
        if(function == null)
            function = "last";

        // GET ASSETS RELATED WITH THE MEASUREMENT (If the assets is a energy island and there isn't category it doesn't filter by asset)

        if (measurement.getAsset() != null && measurement.getAsset().getType() != null &&
                !measurement.getAsset().getType().getName().equalsIgnoreCase("energy_island"))
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
        if(group != null)
            params.put("group", group);
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
        if (assetNames != null && !assetNames.isEmpty())
            params.put("asset_name", assetNames.stream().collect(Collectors.joining(",")));
        if (tags != null && !tags.isEmpty())
            params.putAll(tags.stream()
                    .filter(tag -> !params.containsKey(tag.getValue()))
                    .collect(Collectors.toMap(Details::getKey, Details::getValue)));

        // PARSE TO NON CUMULATIVE DATA IF THE MEASUREMENT IS CUMULATIVE
        MeasurementDetails cumulative = null;
        if (measurement.getDetails() != null)
            cumulative = measurement.getDetails().stream().filter(
                    details -> details.getKey().equalsIgnoreCase("cumulative")).findFirst().orElse(null);

        if (cumulative == null && cumulativeTypes.contains(measurement.getType().getPhysicalName())) {
            params.put("performDecumulation", "true");
        } else if (cumulative != null) {
            params.put("performDecumulation", cumulative.getValue());
        }

        // INFLUX API REQUEST
        HttpResponse<String> response =
                HttpAPIs.sendRequest(influxURL + "/api/measurement/data/" + function, "GET", params, null,
                        null);

        if (response.statusCode() < 300) {
            JSONArray array = new JSONArray(response.body());

            DataResponse dataResponse = new DataResponse();
            dataResponse.setRawResult(array);
            DataFields lastData =  new DataFields();

            if (array.length() > 0) {
                String finalFunction = function;
                array.forEach(obj -> {
                    if (obj instanceof JSONObject) {
                        JSONObject json = (JSONObject) obj;
                        if (json.has("measurement")){
                            String time = json.getJSONObject("fields").getString("time");
                            if(lastData.getTime() == null || lastData.getTime().compareTo(time) < 0){
                                lastData.setTime(time);
                                lastData.setValue(json.getJSONObject("fields").getString(finalFunction));
                            }

                        }
                    }
                });
            }
            dataResponse.setDataUsedForComparison(lastData);
            return dataResponse;
        }
        return new DataResponse();
    }
}
