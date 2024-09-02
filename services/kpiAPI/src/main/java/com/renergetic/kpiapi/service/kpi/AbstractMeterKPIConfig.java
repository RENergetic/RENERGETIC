package com.renergetic.kpiapi.service.kpi;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.InfluxFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AbstractMeterKPIConfig {

    AbstractMeter abstractMeter;
    InfluxFunction function;
    int period = 0; //period (interval offset) `0` STORAGE current, '-1' STORAGE one interval before, '2' -> to ahead

    String getKey() {
        return this.abstractMeter.meterLabel + "_" + function.name() + " " + period;
    }

}
