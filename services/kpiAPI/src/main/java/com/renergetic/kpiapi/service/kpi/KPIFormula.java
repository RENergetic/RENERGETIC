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


//    #region old formulas

//    public BigDecimal calculateESS(Map<AbstractMeter, Double> values) {
//
//        Double result = (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE) - (values.get(AbstractMeter.ENS) + values.get(AbstractMeter.ERS))) / (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));
//
//        if (!Double.isNaN(result) && !Double.isInfinite(result)) return BigDecimal.valueOf(result);
//        else return new BigDecimal(0);
//    }
//
//    public BigDecimal calculateESC(Map<AbstractMeter, Double> values) {
//
//        double result = (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE)) / (values.get(AbstractMeter.LNS) + values.get(AbstractMeter.LRS));
//
//        if (!Double.isNaN(result) && !Double.isInfinite(result)) return BigDecimal.valueOf(result);
//        else return new BigDecimal(0);
//    }
//
//    public BigDecimal calculateEP(Map<AbstractMeter, Double> values) {
//
//        double result = (values.get(AbstractMeter.EXCESS) + values.get(AbstractMeter.LOSSES) + (values.get(AbstractMeter.ENS) + values.get(AbstractMeter.ERS))) / (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));
//
//        if (!Double.isNaN(result) && !Double.isInfinite(result)) return BigDecimal.valueOf(result);
//        else return new BigDecimal(0);
//    }
//
//    public BigDecimal calculateEE(Map<AbstractMeter, Double> values) {
//
//        double result = 1 - (values.get(AbstractMeter.LOSSES) / (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE)));
//
//        if (!Double.isNaN(result) && !Double.isInfinite(result)) return BigDecimal.valueOf(result);
//        else return new BigDecimal(0);
//    }

//    public BigDecimal calculateES(Map<AbstractMeter, Double> values, Map<AbstractMeter, Double> previousValues) {
//
//        double result = ((previousValues.get(AbstractMeter.LOAD) + previousValues.get(AbstractMeter.LOSSES) + previousValues.get(AbstractMeter.STORAGE)) - (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE))) / (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));
//
//        if (!Double.isNaN(result) && !Double.isInfinite(result)) return BigDecimal.valueOf(result);
//        else return new BigDecimal(0);
//    }

//    public BigDecimal calculateSRES(Map<AbstractMeter, Double> values) {
//        //in the original equation storage was subtracted in the nominator , in order to preserve the renewables values between 0-1 we need to add all storage in the denominator
//        double result = (values.get(AbstractMeter.LRS) + values.get(AbstractMeter.ERS) + values.get(AbstractMeter.RES)) / (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));
//
//        if (!Double.isNaN(result) && !Double.isInfinite(result)) return BigDecimal.valueOf(result);
//        else return new BigDecimal(0);
//    }

//    public BigDecimal calculateSNES(Map<AbstractMeter, Double> values) {
//        double result = 1 - ((values.get(AbstractMeter.LRS) + values.get(AbstractMeter.ERS) + values.get(AbstractMeter.RES)) / (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE)));
//
//        if (!Double.isNaN(result) && !Double.isInfinite(result)) return BigDecimal.valueOf(result);
//        else return new BigDecimal(0);
//    }
//
//    public BigDecimal calculateCO2(Map<AbstractMeter, Double> values) {

//        KPIConstant c = constantRepository.findAll().stream().findFirst().orElse(new KPIConstant(1L, 1., 1., 1., 1.));
//
//        log.debug(String.format("Constants: a -> %.2f | b -> %.2f | g -> %.2f | d -> %.2f", c.getAlpha(), c.getBeta(), c.getGamma(), c.getDelta()));
//
//        Double result = ((c.getAlpha() * values.get(AbstractMeter.LRS) + c.getBeta() * values.get(AbstractMeter.ERS) + c.getGamma() * values.get(AbstractMeter.ENS) + c.getDelta() * values.get(AbstractMeter.LNS))) / (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE));
//
//        if (!Double.isNaN(result) && !Double.isInfinite(result)) return BigDecimal.valueOf(result);
//        else return new BigDecimal(0);
//    }
//
//    public BigDecimal calculatePEAK(Map<AbstractMeter, Double> values) {
//
//        Double result = values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE);
//
//        if (!Double.isNaN(result) && !Double.isInfinite(result)) return BigDecimal.valueOf(result);
//        else return new BigDecimal(0);
//    }

    //endregion
}


