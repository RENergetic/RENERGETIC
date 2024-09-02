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
public class CO2 implements KPIFormula {
    @Autowired
    private KPIConstantRepository constantRepository;
    public final static CO2 Instance = new CO2();

    @Override
    public KPI getKPI() {
        return KPI.CO2;
    }

    private static AbstractMeterKPIConfig[] requiredMeters = {
            new AbstractMeterKPIConfig( AbstractMeter.LRS, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.ENS, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.ERS, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.LNS, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.LOAD, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.LOSSES, InfluxFunction.SUM,0),
            new AbstractMeterKPIConfig( AbstractMeter.STORAGE, InfluxFunction.SUM,0)

    };


    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values, Map<AbstractMeter, Double> previous) {
        return this.calculate(values);
    }
    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values) {
////        todo review this kpi
        KPIConstant c = constantRepository.findAll().stream().findFirst().orElse(new KPIConstant(1L, 1., 1., 1., 1.));

//        log.debug(String.format("Constants: a -> %.2f | b -> %.2f | g -> %.2f | d -> %.2f", c.getAlpha(), c.getBeta(), c.getGamma(), c.getDelta()));

        Double result = ((c.getAlpha() * values.get(AbstractMeter.LRS) + c.getBeta() * values.get(AbstractMeter.ERS) +
                c.getGamma() * values.get(AbstractMeter.ENS) + c.getDelta() * values.get(AbstractMeter.LNS))) /
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
