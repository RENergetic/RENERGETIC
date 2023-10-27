package com.renergetic.ruleevaluationservice.dao.muveco;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MuVeCoConfigDAO {
    @JsonProperty(value = "measurements_configs", required = true)
    private List<MuVeCoMeasurementConfigDAO> measurementConfig;
    @JsonProperty(value = "parameters_configs", required = true)
    private List<MuVeCoParameterConfigDAO> parameterConfig;
}
