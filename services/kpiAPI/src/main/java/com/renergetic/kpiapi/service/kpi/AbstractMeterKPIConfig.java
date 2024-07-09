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
    int period = 0; //period t0 - current, '-t1' one interval before,

    String getKey() {
        return this.abstractMeter.meter + "_" + function.name() + " " + period;
    }

}
