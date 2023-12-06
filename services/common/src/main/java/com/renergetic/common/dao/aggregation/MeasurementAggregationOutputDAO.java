package com.renergetic.common.dao.aggregation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.details.MeasurementDetails;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class MeasurementAggregationOutputDAO {
    @JsonProperty(value = "aggregationType", required = true)
    private String aggregationType;
    @JsonProperty(value = "timeMin", required = true)
    private String timeMin;
    @JsonProperty(value = "timeMax", required = true)
    private String timeMax;
    @JsonProperty(value = "timeRange", required = true)
    private String timeRange;

    public static MeasurementAggregationOutputDAO create(Measurement measurement){
        MeasurementAggregationOutputDAO measurementAggregationOutputDAO = new MeasurementAggregationOutputDAO();

        if(measurement != null){
            measurementAggregationOutputDAO.setAggregationType(measurement.getDetails().stream()
                    .filter(x -> x.getKey().equals("aggregation_type")).findFirst().orElse(new MeasurementDetails()).getValue());
            measurementAggregationOutputDAO.setTimeMin(measurement.getDetails().stream()
                    .filter(x -> x.getKey().equals("time_min")).findFirst().orElse(new MeasurementDetails()).getValue());
            measurementAggregationOutputDAO.setTimeMax(measurement.getDetails().stream()
                    .filter(x -> x.getKey().equals("time_max")).findFirst().orElse(new MeasurementDetails()).getValue());
            measurementAggregationOutputDAO.setTimeRange(measurement.getDetails().stream()
                    .filter(x -> x.getKey().equals("time_range")).findFirst().orElse(new MeasurementDetails()).getValue());
        }

        return measurementAggregationOutputDAO;
    }
}
