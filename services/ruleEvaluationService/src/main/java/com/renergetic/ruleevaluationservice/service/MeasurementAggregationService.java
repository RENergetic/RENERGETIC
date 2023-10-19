package com.renergetic.ruleevaluationservice.service;

import com.google.gson.Gson;
import com.renergetic.common.dao.MeasurementDAO;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.ruleevaluationservice.dao.MeasurementAggregationDataDAO;
import com.renergetic.ruleevaluationservice.dao.MeasurementSimplifiedDAO;
import com.renergetic.ruleevaluationservice.service.utils.HttpAPIs;
import com.renergetic.ruleevaluationservice.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeasurementAggregationService {
    @Value("${ingestion.api.url}")
    String ingestionAPI;
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private DataService dataService;

    private final Gson gson = new Gson();

    public void aggregateAll(){
        List<Measurement> measurements = retrieveMeasurementsToAggregate();
        for(Measurement measurement : measurements){
            aggregateOne(measurement);
        }
    }

    public void aggregateOne(Measurement measurement){
        List<MeasurementSimplifiedDAO> aggregated = getAggregatedData(measurement);
        publishAggregatedData(aggregated);
    }

    public boolean isMeasurementToAggregate(Measurement measurement){
        return measurement.getDetails().stream()
                .anyMatch(d -> d.getKey().contains("aggregation_ids"));
    }

    private List<Measurement> retrieveMeasurementsToAggregate(){
        //TODO: Later replace the query to directly fetch the one we want.
        return measurementRepository.findAll().stream().filter(this::isMeasurementToAggregate).collect(Collectors.toList());
    }

    private List<Measurement> retrieveChildrenMeasurements(List<Long> ids){
        return measurementRepository.findByIds(ids);
    }

    private List<MeasurementSimplifiedDAO> getAggregatedData(Measurement measurement){
        MeasurementAggregationDataDAO mad = new MeasurementAggregationDataDAO();
        for(MeasurementDetails md : measurement.getDetails()){
            switch(md.getKey()){
                case "aggregation_ids":
                    mad.setAggregationIds(List.of(gson.fromJson(md.getValue(), Long[].class)));
                    break;
                case "aggregation_type":
                    mad.setAggregationType(md.getValue());
                    break;
                case "time_min":
                    mad.setTimeMin(md.getValue());
                    break;
                case "time_max":
                    mad.setTimeMax(md.getValue());
                    break;
                case "time_range":
                    mad.setTimeRange(md.getValue());
                    break;
            }
        }

        List<Measurement> children = retrieveChildrenMeasurements(mad.getAggregationIds());
        Long from = null;
        if(mad.getTimeMin().equals("now"))
            from = Instant.now().toEpochMilli();
        else if(mad.getTimeMin().contains("now-"))
            from = TimeUtils.offsetNegativeCurrentInstant(mad.getTimeMin().substring(4)).toEpochMilli();
        else if(mad.getTimeMin().contains("now+"))
            from = TimeUtils.offsetPositiveCurrentInstant(mad.getTimeMin().substring(4)).toEpochMilli();
        Optional<Long> to = Optional.empty();
        if(mad.getTimeMax().equals("now"))
            to = Optional.of(Instant.now().toEpochMilli());
        else if(mad.getTimeMax().contains("now-"))
            to = Optional.of(TimeUtils.offsetNegativeCurrentInstant(mad.getTimeMax().substring(4)).toEpochMilli());
        else if(mad.getTimeMax().contains("now+"))
            to = Optional.of(TimeUtils.offsetPositiveCurrentInstant(mad.getTimeMax().substring(4)).toEpochMilli());

        List<MeasurementSimplifiedDAO> msd = dataService.getData(children, mad.getAggregationType(), mad.getTimeRange(), from, to);

        //Retrieving the original tags
        List<MeasurementTags> dbTags = measurementRepository.getTags(measurement.getId());
        Map<String, String> mapTags = new HashMap<>();
        for(MeasurementTags dbTag : dbTags)
            mapTags.put(dbTag.getKey(), dbTag.getValue());
        mapTags.put("asset_name", measurement.getAsset().getName());

        return msd.stream()
                //Replacing the tags with the original ones.
                //Replacing the field name with the original one.
                .peek(m -> {
                    m.setTags(mapTags);
                    m.setMeasurement(measurement.getSensorName());
                    Map<String, String> fields = m.getFields();
                    fields.put(children.get(0).getType().getName(), fields.get(mad.getAggregationType()));
                    fields.remove(mad.getAggregationType());
                    m.setFields(m.getFields());
                })
                .collect(Collectors.toList());
    }

    private void publishAggregatedData(List<MeasurementSimplifiedDAO> aggregated){
        // TODO: Send the data to the ingestion api, using the call
        // The idea is that from time to time, we may have data missing, so we have to check that the
        // Ingestion API is effectively overwriting existing time point in the timeseries if you add it again,
        // this way this fixes the problem.
        HttpResponse<String> response =
                HttpAPIs.sendRequest(ingestionAPI + "/api/ingest", "POST", null, aggregated,
                        null);
    }
}
