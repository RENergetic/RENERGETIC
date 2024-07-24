package com.renergetic.kpiapi.service.kpi;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.KPIConstant;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface KPIFormula {
    public BigDecimal calculate(Map<AbstractMeter, Double> values,Map<AbstractMeter, Double> previous);
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


//

//
//    public BigDecimal calculateCO2(Map<AbstractMeter, Double> values) {
////        todo review this kpi
//        KPIConstant c = constantRepository.findAll().stream().findFirst().orElse(new KPIConstant(1L, 1., 1., 1., 1.));
//
//        log.debug(String.format("Constants: a -> %.2f | b -> %.2f | g -> %.2f | d -> %.2f", c.getAlpha(), c.getBeta(), c.getGamma(), c.getDelta()));
//
//        Double result = ((c.getAlpha() * values.get(AbstractMeter.LRS) + c.getBeta() * values.get(AbstractMeter.ERS) +
//                c.getGamma() * values.get(AbstractMeter.ENS) + c.getDelta() * values.get(AbstractMeter.LNS))) /
//                (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));
//
//        if (!Double.isNaN(result) && !Double.isInfinite(result))
//            return BigDecimal.valueOf(result);
//        else return new BigDecimal(0);
//    }
//
//    public BigDecimal calculatePEAK(Map<AbstractMeter, Double> values) {
//
//        Double result = values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE);
//
//        if (!Double.isNaN(result) && !Double.isInfinite(result))
//            return BigDecimal.valueOf(result);
//        else return new BigDecimal(0);
//    }
}


