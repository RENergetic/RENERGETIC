package com.renergetic.ruleevaluationservice.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MeasurementAggregationDataDAO {
    private List<Long> aggregationIds;
    private String aggregationType;
    private String timeMin;
    private String timeMax;
    private String timeRange;
}
