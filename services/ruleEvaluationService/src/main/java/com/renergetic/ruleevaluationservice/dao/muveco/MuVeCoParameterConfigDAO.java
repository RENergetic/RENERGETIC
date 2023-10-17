package com.renergetic.ruleevaluationservice.dao.muveco;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MuVeCoParameterConfigDAO {
    @JsonProperty(value = "parameter", required = true)
    private String parameter;
    @JsonProperty(value = "aggregation", required = true)
    private String aggregation;
}
