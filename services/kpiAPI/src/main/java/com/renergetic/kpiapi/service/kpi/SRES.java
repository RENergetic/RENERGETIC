package com.renergetic.kpiapi.service.kpi;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.InfluxFunction;
import com.renergetic.kpiapi.model.KPI;
import com.renergetic.kpiapi.model.KPIConstant;
import com.renergetic.kpiapi.repository.KPIConstantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class SRES implements KPIFormula {
    @Autowired
    private KPIConstantRepository constantRepository;
    public final static SRES Instance = new SRES();

    @Override
    public KPI getKPI() {
        return KPI.SRES;
    }

    private static AbstractMeterKPIConfig[] requiredMeters = {
            new AbstractMeterKPIConfig(AbstractMeter.LRS, InfluxFunction.SUM, 0),
            new AbstractMeterKPIConfig(AbstractMeter.ERS, InfluxFunction.SUM, 0),
            new AbstractMeterKPIConfig(AbstractMeter.LOAD, InfluxFunction.SUM, 0),
            new AbstractMeterKPIConfig(AbstractMeter.LOSSES, InfluxFunction.SUM, 0),
            new AbstractMeterKPIConfig(AbstractMeter.STORAGE, InfluxFunction.SUM, 0),
            new AbstractMeterKPIConfig(AbstractMeter.RES, InfluxFunction.SUM, 0)
    };

    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values, Map<AbstractMeter, Double> previous) {
        return this.calculate(values);
    }

    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values) {
        double result = (values.get(AbstractMeter.LRS) + values.get(AbstractMeter.ERS)
                + values.get(AbstractMeter.RES)) / (values.get(AbstractMeter.LOAD)
                + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));

        if (!Double.isNaN(result) && !Double.isInfinite(result)) return BigDecimal.valueOf(result);
        else return new BigDecimal(0);
    }

    @Override
    public AbstractMeterKPIConfig[] getRequiredAbstractMeters() {
        return requiredMeters;
    }


}
