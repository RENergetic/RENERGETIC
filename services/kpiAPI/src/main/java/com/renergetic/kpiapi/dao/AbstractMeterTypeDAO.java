package com.renergetic.kpiapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.kpiapi.model.AbstractMeter;
import lombok.Getter;

@Getter
public class AbstractMeterTypeDAO {

    @Deprecated
    String meter;
    String label;
    String description;
    String name;

    @JsonProperty(value = "physical_name")
    String physicalName;

    public AbstractMeterTypeDAO(AbstractMeter meter) {
        this.name = meter.name();
        this.meter = meter.meterLabel;
        this.label = meter.meterLabel;
        this.description = meter.description;
        this.physicalName = meter.physicalName;
    }

}
