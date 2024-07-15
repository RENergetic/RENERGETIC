package com.renergetic.kpiapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.kpiapi.model.AbstractMeter;
import lombok.Getter;

@Getter
public class AbstractMeterTypeDAO {

    String meter;
    String description;

    @JsonProperty(value = "physical_name")
    String physicalName;

    public AbstractMeterTypeDAO(AbstractMeter meter) {
        this.meter = meter.meter;
        this.description = meter.description;
        this.physicalName = meter.physicalName;
    }

}
