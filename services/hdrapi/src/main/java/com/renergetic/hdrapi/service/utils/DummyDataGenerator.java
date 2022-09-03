package com.renergetic.hdrapi.service.utils;

import com.renergetic.hdrapi.dao.DataDAO;
import com.renergetic.hdrapi.dao.DemandScheduleDAO;
import com.renergetic.hdrapi.dao.MeasurementDAOResponse;
import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.DemandDefinition;
import com.renergetic.hdrapi.model.DemandSchedule;
import com.renergetic.hdrapi.model.Measurement;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class DummyDataGenerator {
    private static final Random random = new Random();

    private static Double getMeasurementValue(Measurement m) {
//TODO: consider measurement type and domains
        return (random.nextInt(300 * 100)) / 100.0 + 200.0;
    }

    private static Double getMeasurementValue(MeasurementDAOResponse m) {
//TODO: consider measurement type and domains
        return (random.nextInt(300 * 100)) / 100.0 + 200.0;
    }

    public static DataDAO getDemandData(List<DemandScheduleDAO> demands) {
        DataDAO data = new DataDAO();
        Map<String, Double> measurementValues = demands.stream().filter(
                it-> it.getDemandDefinition().getTile()!=null
        )
                .flatMap(demand -> demand.getDemandDefinition().getTile().getMeasurements().stream())
                .collect(Collectors.toMap(it -> it.getId().toString(), DummyDataGenerator::getMeasurementValue,
                        (a1, a2) -> a1));

        data.getCurrent().setLast(measurementValues);

        return data;
    }

    public static DataDAO getData(List<Measurement> measurements) {
        DataDAO data = new DataDAO();
        Map<String, Double> measurementValues = measurements.stream().collect(
                Collectors.toMap(it -> it.getId().toString(), DummyDataGenerator::getMeasurementValue, (a1, a2) -> a1));
        data.getCurrent().setLast(measurementValues);
        return data;
    }

    public static List<DemandScheduleDAO> getDemand(List<DemandDefinition> demands, List<Asset> assets) {
        long current = (new Date()).getTime();

        return demands.stream().flatMap(
                def -> assets.stream().map(
                        asset -> {
                            DemandSchedule demandSchedule = new DemandSchedule();
                            demandSchedule.setAsset(asset);
                            demandSchedule.setDemandDefinition(def);
                            demandSchedule.setDemandStart(DateConverter.toLocalDateTime(current - 3600000));
                            demandSchedule.setDemandStop(DateConverter.toLocalDateTime(current + 3600000));
                            return demandSchedule;
                        }
                )

        ).map(DemandScheduleDAO::create).collect(Collectors.toList());
    }

    public static List<DemandScheduleDAO> getDemand(List<DemandScheduleDAO> schedule) {
        long current = (new Date()).getTime();
        return schedule.stream().peek(s -> {
                    s.setDemandStart(current - 3600000);
                    s.setDemandStop(current + 3600000);
                }
        ).collect(Collectors.toList());
    }

}
//    function generatePanelData(informationPanel, predictionWindow) {
//        let measurements = listMeasurements(informationPanel);
//
//        let data = {};
//        for (let m of Object.keys(measurements)) {
//            let value = Math.floor(Math.random() * 150);
//            data[m] = value;
//        }
//        if (predictionWindow == null) return { current: { default: data } };
//        return { prediction: { default: data } };
//    }

/**
 * {
 * "data": {
 * "current": {
 * "default": {
 * "1": 33
 * },
 * "max": {
 * "1": 33
 * }
 * },
 * "prediction": {
 * "default": {
 * "1": 33
 * },
 * "max": {
 * "1": 33
 * }
 * }
 * }
 */