package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.MeasurementType;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class MeasurementTypeDAORequest {
    //TODO: store symbol ,"ºC",
    @JsonProperty(required = false)
    private Long id;

    @JsonProperty(required = true, value = "physical_name")
    private String physicalName;

    public static MeasurementTypeDAORequest create(MeasurementType m) {
        return new MeasurementTypeDAORequest(m.getId(), m.getPhysicalName());
    }

}
