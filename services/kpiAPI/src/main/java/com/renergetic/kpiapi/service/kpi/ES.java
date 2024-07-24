package com.renergetic.kpiapi.service.kpi;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.InfluxFunction;

import java.math.BigDecimal;
import java.util.Map;

public class EE implements KPIFormula {

    public final static EE Instance = new EE();

    private static final AbstractMeterKPIConfig[] requiredMeters = {
            new AbstractMeterKPIConfig( AbstractMeter.LOSSES, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.LOAD, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.STORAGE, InfluxFunction.SUM,0),
    };

    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values) {
        double result = 1 - (values.get(AbstractMeter.LOSSES) /
                (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE)));

        if (!Double.isNaN(result) && !Double.isInfinite(result))
            return BigDecimal.valueOf(result);
        else return new BigDecimal(0);
    }

    @Override
    public AbstractMeterKPIConfig[] getRequiredAbstractMeters() {
        return requiredMeters;
    }




}
