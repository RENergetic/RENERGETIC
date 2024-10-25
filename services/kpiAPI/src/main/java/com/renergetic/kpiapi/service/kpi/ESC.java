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

    private static final AbstractMeterKPIConfig[] requiredMeters = {
            new AbstractMeterKPIConfig(AbstractMeter.LOAD, InfluxFunction.SUM, 0),
            new AbstractMeterKPIConfig(AbstractMeter.LOSSES, InfluxFunction.SUM, 0),
            new AbstractMeterKPIConfig(AbstractMeter.STORAGE, InfluxFunction.SUM, 0),
            new AbstractMeterKPIConfig(AbstractMeter.LRS, InfluxFunction.SUM, 0),
            new AbstractMeterKPIConfig(AbstractMeter.LNS, InfluxFunction.SUM, 0)

    };

    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values, Map<AbstractMeter, Double> previous) {
        return this.calculate(values);
    }

    @Override
    public BigDecimal calculate(Map<AbstractMeter, Double> values) {
//        Double result = (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE)) /
//                (values.get(AbstractMeter.LNS) + values.get(AbstractMeter.LRS) );
        double result = (values.get(AbstractMeter.LNS) + values.get(AbstractMeter.LRS))
                /
                ((values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.LOSSES) + values.get(AbstractMeter.STORAGE)));
        ;
//        double result = (values.get(AbstractMeter.LOAD) + values.get(AbstractMeter.STORAGE)-  values.get(AbstractMeter.LOSSES)
//                - values.get(AbstractMeter.ERS) - values.get(AbstractMeter.ENS)) /
//                (values.get(AbstractMeter.LNS) + values.get(AbstractMeter.LRS));


//        from(bucket: "renergetic")
//  |> range(start: v.timeRangeStart, stop: v.timeRangeStop)
//  |> filter(fn: (r) => r["_measurement"] == "abstract_meter")
//  |> filter(fn: (r) => r["measurement_type"] == "losses" or r["measurement_type"] == "ers" or r["measurement_type"] == "ens" or r["measurement_type"] == "lrs" or r["measurement_type"] == "lns" or r["measurement_type"] == "load")
//  |> filter(fn: (r) => r["domain"] == "none")
//  |> aggregateWindow(every: 12h, fn: sum, createEmpty: false)
//   |> keep(columns: ["measurement_type", "_time", "_value"])
// |> pivot(rowKey:["_time"], columnKey: ["measurement_type"], valueColumn: "_value")
// |> map(fn: (r) => ({r with _value:( r["load"] - r["ers"]-r["ens"])/(r["lrs"]+r["lns"])*100.0}))
//   |> keep(columns: [  "_time", "_value"])
//  |> yield(name: "mean")
        if (!Double.isNaN(result) && !Double.isInfinite(result))
            return BigDecimal.valueOf(result);
        else return new BigDecimal(0);
    }

    @Override
    public AbstractMeterKPIConfig[] getRequiredAbstractMeters() {
        return requiredMeters;
    }


}
