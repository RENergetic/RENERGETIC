package com.renergetic.kpiapi.service.kpi;

import com.renergetic.kpiapi.exception.InvalidArgumentException;
import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.InfluxFunction;
import com.renergetic.kpiapi.model.KPI;

import java.math.BigDecimal;
import java.util.Map;

public class ES implements KPIFormula {

    public final static ES Instance = new ES();
    @Override
    public KPI getKPI() {
        return KPI.ES;
    }

    private static final AbstractMeterKPIConfig[] requiredMeters = {
            new AbstractMeterKPIConfig(AbstractMeter.LOAD, InfluxFunction.SUM, -1),
            new AbstractMeterKPIConfig(AbstractMeter.LOSSES, InfluxFunction.SUM, -1),
            new AbstractMeterKPIConfig(AbstractMeter.STORAGE, InfluxFunction.SUM, -1),
            new AbstractMeterKPIConfig(AbstractMeter.LOAD, InfluxFunction.SUM, 0),
            new AbstractMeterKPIConfig(AbstractMeter.LOSSES, InfluxFunction.SUM, 0),
            new AbstractMeterKPIConfig(AbstractMeter.STORAGE, InfluxFunction.SUM, 0),
    };

    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values, Map<AbstractMeter, Double> previous) {
        Double result = ((previous.get(AbstractMeter.LOAD) + previous.get(AbstractMeter.LOSSES) + previous.get(AbstractMeter.STORAGE)) -
                (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE))) /
                (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));

        if (!Double.isNaN(result) && !Double.isInfinite(result))
            return BigDecimal.valueOf(result);
        else return new BigDecimal(0);
    }

    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values) {
    throw  new InvalidArgumentException("This KPI requires previous values");
    }

    @Override
    public AbstractMeterKPIConfig[] getRequiredAbstractMeters() {
        return requiredMeters;
    }


}
