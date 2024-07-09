package com.renergetic.kpiapi.service.kpi;

import com.renergetic.kpiapi.model.AbstractMeter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface KPIFormula {
    public BigDecimal calculate(Map<AbstractMeter, Double> values);

    public AbstractMeterKPIConfig[] getRequiredAbstractMeters();


    public static AbstractMeterKPIConfig[] getRequiredAbstractMeters(List<KPIFormula> kpis) {
        Map<String, AbstractMeterKPIConfig> m = new HashMap<>();
        for (var kpi : kpis) {
            for (var meter : kpi.getRequiredAbstractMeters()) {
                m.put(meter.getKey(), meter);
            }
        }
        return m.values().toArray(new AbstractMeterKPIConfig[0]);
    }


}


