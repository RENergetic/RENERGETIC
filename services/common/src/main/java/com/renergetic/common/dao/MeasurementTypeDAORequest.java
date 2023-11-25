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
    @JsonProperty(required = false, value = "name")
    private String name;

    @JsonProperty(required = false, value = "physical_name")
    private String physicalName;

    @JsonProperty(required = false, value = "unit")
    private String unit;


    public static MeasurementTypeDAORequest create(MeasurementType m) {
        return new MeasurementTypeDAORequest(m.getId(),m.getName(), m.getPhysicalName(), m.getUnit());
    }

}
