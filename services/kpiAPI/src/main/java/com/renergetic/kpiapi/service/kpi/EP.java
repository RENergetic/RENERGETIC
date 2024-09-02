package com.renergetic.kpiapi.service.kpi;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.InfluxFunction;
import com.renergetic.kpiapi.model.KPI;

import java.math.BigDecimal;
import java.util.Map;

public class EP implements KPIFormula {

    public final static EP Instance = new EP();
    @Override
    public KPI getKPI() {
        return KPI.EP;
    }

    private static final AbstractMeterKPIConfig[] requiredMeters = {
            new AbstractMeterKPIConfig( AbstractMeter.EXCESS, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.ENS, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.LOSSES, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.LOAD, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.STORAGE, InfluxFunction.SUM,0),
    };
    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values, Map<AbstractMeter, Double> previous) {
        return this.calculate(values);
    }
    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values) {
        Double result = (values.get(AbstractMeter.EXCESS) + values.get(AbstractMeter.LOSSES) +
                (values.get(AbstractMeter.ENS) + values.get(AbstractMeter.ERS))) /
                (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));

        if (!Double.isNaN(result) && !Double.isInfinite(result))
            return BigDecimal.valueOf(result);
        else return new BigDecimal(0);
    }

    @Override
    public AbstractMeterKPIConfig[] getRequiredAbstractMeters() {
        return requiredMeters;
    }




}
