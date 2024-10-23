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
public class MeasurementTypeDAORequest {

    /**
     * ID is optional when object is used tp infer measurement type (for example during import/export of dashboards, where id's might be different for pilotss) -
     * the id of the measurement type (or list of  measurement type ids ) is inferred from other fields
     */
    @JsonProperty(required = false)
    private Long id;
    @JsonProperty(required = false, value = "name")
    private String name;

    @JsonProperty(required = false, value = "physical_name")
    private String physicalName;

    @JsonProperty(required = false, value = "unit")
    private String unit;


    public static MeasurementTypeDAORequest create(MeasurementType m) {
        return new MeasurementTypeDAORequest(m.getId(), m.getName(), m.getPhysicalName(), m.getUnit());
    }

    public MeasurementType mapToEntity() {
        if (this.getId() == null) {
            if (this.name == null && this.physicalName == null && this.unit == null) {
                throw new InvalidArgumentException("Missing at least one non nullable field");
            }
        }
        var mt = new MeasurementType();
        mt.setPhysicalName(this.getPhysicalName());
        mt.setId(this.getId());
        mt.setName(this.getName());
        mt.setUnit(this.getUnit());
        return mt;
    }

}
