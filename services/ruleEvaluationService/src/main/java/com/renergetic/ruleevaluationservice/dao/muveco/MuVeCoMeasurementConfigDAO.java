package com.renergetic.ruleevaluationservice.dao.muveco;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MuVeCoMeasurementConfigDAO {
    @JsonProperty(value = "muveco_name", required = true)
    private String name;
    @JsonProperty(value = "measurements", required = true)
    private List<Long> measurements;
    @JsonProperty(value = "outputs", required = true)
    private List<MuVeCoOutputConfigDAO> outputs;
}
