package com.renergetic.kpiapi.service.kpi;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.InfluxFunction;

import java.math.BigDecimal;
import java.util.Map;

public class ESS implements KPIFormula {

    public final static ESS Instance = new ESS();

    private static AbstractMeterConfig[] requiredMeters = {
            new  AbstractMeterConfig( AbstractMeter.LOAD, InfluxFunction.SUM,0),
            new  AbstractMeterConfig( AbstractMeter.LOSSES, InfluxFunction.SUM,0),
            new  AbstractMeterConfig( AbstractMeter.STORAGE, InfluxFunction.SUM,0),
            new  AbstractMeterConfig( AbstractMeter.ENS, InfluxFunction.SUM,0),
            new  AbstractMeterConfig( AbstractMeter.ERS, InfluxFunction.SUM,0)

    };

    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values) {
        Double result = (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE) -
                (values.get(AbstractMeter.ENS) + values.get(AbstractMeter.ERS))) /
                (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));

        if (!Double.isNaN(result) && !Double.isInfinite(result))
            return BigDecimal.valueOf(result);
        else return new BigDecimal(0);
    }

    @Override
    public AbstractMeterConfig[] getRequiredAbstractMeters() {
        return requiredMeters;
    }


}
