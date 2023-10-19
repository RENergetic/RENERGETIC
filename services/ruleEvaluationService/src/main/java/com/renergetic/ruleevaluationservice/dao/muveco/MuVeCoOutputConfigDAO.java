package com.renergetic.ruleevaluationservice.dao.muveco;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MuVeCoOutputConfigDAO {
    @JsonProperty(value = "measurement_name", required = true)
    private String measurementName;
    @JsonProperty(value = "aggregation_type", required = true)
    private String aggregationType;
    @JsonProperty(value = "time_min", required = true)
    private String timeMin;
    @JsonProperty(value = "time_max", required = true)
    private String timeMax;
    @JsonProperty(value = "time_range", required = true)
    private String timeRange;
}
