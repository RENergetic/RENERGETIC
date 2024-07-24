package com.renergetic.kpiapi.service.kpi;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.InfluxFunction;

import java.math.BigDecimal;
import java.util.Map;

public class PEAK implements KPIFormula {

    public final static PEAK Instance = new PEAK();

    private static AbstractMeterKPIConfig[] requiredMeters = {
            new AbstractMeterKPIConfig( AbstractMeter.LOSSES, InfluxFunction.MAX,0),
            new AbstractMeterKPIConfig( AbstractMeter.STORAGE, InfluxFunction.MAX,0),
            new AbstractMeterKPIConfig( AbstractMeter.LOAD ,InfluxFunction.MAX,0)

    };

    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values, Map<AbstractMeter, Double> previous) {
        return this.calculate(values);
    }
    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values) {
                //in the original equation storage was subtracted in the nominator , in order to preserve the renewables values between 0-1 we need to add all storage in the denominator
        Double result = values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE);

        if (!Double.isNaN(result) && !Double.isInfinite(result))
            return BigDecimal.valueOf(result);
        else return new BigDecimal(0);
    }

    @Override
    public AbstractMeterKPIConfig[] getRequiredAbstractMeters() {
        return requiredMeters;
    }




}
