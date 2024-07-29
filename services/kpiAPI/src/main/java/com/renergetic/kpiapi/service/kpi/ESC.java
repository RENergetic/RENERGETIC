package com.renergetic.kpiapi.service.kpi;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.InfluxFunction;
import com.renergetic.kpiapi.model.KPI;

import java.math.BigDecimal;
import java.util.Map;

public class ESC implements KPIFormula {

    public final static ESC Instance = new ESC();
    @Override
    public KPI getKPI() {
        return KPI.ESC;
    }

    private static AbstractMeterKPIConfig[] requiredMeters = {
            new AbstractMeterKPIConfig( AbstractMeter.LOAD, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.LOSSES, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.STORAGE, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.LRS, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.LNS, InfluxFunction.SUM,0)

    };

    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values, Map<AbstractMeter, Double> previous) {
        return this.calculate(values);
    }
    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values) {
        Double result = (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE)) /
                (values.get(AbstractMeter.LNS) + values.get(AbstractMeter.LRS) );

        if (!Double.isNaN(result) && !Double.isInfinite(result))
            return BigDecimal.valueOf(result);
        else return new BigDecimal(0);
    }

    @Override
    public AbstractMeterKPIConfig[] getRequiredAbstractMeters() {
        return requiredMeters;
    }




}
