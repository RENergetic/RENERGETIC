package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.exception.InvalidArgumentException;
import com.renergetic.common.model.MeasurementType;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class MeasurementTypeDAO {

    @JsonProperty(required = true)
    private Long id;
    @JsonProperty(required = false, value = "name")
    private String name;

    @JsonProperty(required = false, value = "physical_name")
    private String physicalName;

    @JsonProperty(required = false, value = "unit")
    private String unit;


    public static MeasurementTypeDAO create(MeasurementType m) {
        return new MeasurementTypeDAO(m.getId(), m.getName(), m.getPhysicalName(), m.getUnit());
    }

    public MeasurementType mapToEntity() {
        var mt = new MeasurementType();
        mt.setPhysicalName(this.getPhysicalName());
        mt.setId(this.getId());
        mt.setName(this.getName());
        mt.setUnit(this.getUnit());
        return mt;
    }

}
