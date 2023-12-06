package com.renergetic.common.dao.aggregation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.MeasurementAggregation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MeasurementAggregationDAO {
    @JsonProperty(value = "measurements", required = true)
    private List<Long> measurements;
    @JsonProperty(value = "outputs", required = true)
    private List<MeasurementAggregationOutputDAO> outputs;

    public static List<MeasurementAggregationDAO> create(List<MeasurementAggregation> measurementAggregations){
        List<MeasurementAggregationDAO> measurementAggregationDAOS = new ArrayList<>();
        if(measurementAggregations != null){
            for(MeasurementAggregation measurementAggregation : measurementAggregations){
                measurementAggregationDAOS.add(create(measurementAggregation));
            }
        }
        return measurementAggregationDAOS;
    }

    public static MeasurementAggregationDAO create(MeasurementAggregation measurementAggregation){
        MeasurementAggregationDAO measurementAggregationDAO = new MeasurementAggregationDAO();

        if(measurementAggregation.getAggregatedMeasurements() != null)
            measurementAggregationDAO.setMeasurements(measurementAggregation.getAggregatedMeasurements()
                    .stream().map(Measurement::getId).collect(Collectors.toList()));
        else
            measurementAggregationDAO.setMeasurements(new ArrayList<>());

        if(measurementAggregation.getOutputMeasurements() != null)
            measurementAggregationDAO.setOutputs(measurementAggregation.getOutputMeasurements().stream()
                    .map(MeasurementAggregationOutputDAO::create).collect(Collectors.toList()));
        else
            measurementAggregationDAO.setOutputs(new ArrayList<>());

        return measurementAggregationDAO;
    }
}
