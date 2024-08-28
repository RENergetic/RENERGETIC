package com.renergetic.kpiapi.service.kpi;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.KPI;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface KPIFormula {
    public BigDecimal calculate(Map<AbstractMeter, Double> values, Map<AbstractMeter, Double> previous);

    public BigDecimal calculate(Map<AbstractMeter, Double> values);

    public AbstractMeterKPIConfig[] getRequiredAbstractMeters();
    public KPI getKPI();


    public static AbstractMeterKPIConfig[] getRequiredAbstractMeters(List<KPIFormula> kpis) {
        Map<String, AbstractMeterKPIConfig> m = new HashMap<>();
        for (var kpi : kpis) {
            for (var meter : kpi.getRequiredAbstractMeters()) {
                m.put(meter.getKey(), meter);
            }
        }
        return m.values().toArray(new AbstractMeterKPIConfig[0]);
    }

    public static List<KPIFormula> listAll() {
        return List.of(ESS.Instance, CO2.Instance, EE.Instance, EP.Instance, ES.Instance, PEAK.Instance);
    }
}


