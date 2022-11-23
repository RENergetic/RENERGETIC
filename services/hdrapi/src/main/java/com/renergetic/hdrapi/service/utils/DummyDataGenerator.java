package com.renergetic.hdrapi.service.utils;

import com.renergetic.hdrapi.dao.DataDAO;
import com.renergetic.hdrapi.dao.DemandScheduleDAO;
import com.renergetic.hdrapi.dao.MeasurementDAOResponse;
import com.renergetic.hdrapi.dao.NotificationDAO;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.model.NotificationType;

import java.util.*;
import java.util.stream.Collectors;

public class DummyDataGenerator {
    private static final Random random = new Random();
    static final String s =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce tellus nisl, finibus condimentum " +
                    "nisl vel, scelerisque venenatis ex. Vivamus pellentesque suscipit nunc id pulvinar. Aliquam vel" +
                    " hendrerit turpis. Orci varius natoque penatibus et magnis dis parturient montes, nascetur " +
                    "ridiculus mus. Aenean efficitur elementum mollis. Donec vel metus eu justo congue commodo";

    private static Double getMeasurementValue(Measurement m) {
//TODO: consider measurement type and domains
        return (random.nextInt(300 * 100)) / 100.0 + 200.0;
    }

    private static Double getMeasurementValue(MeasurementDAOResponse m) {
//TODO: consider measurement type and domains
        return (random.nextInt(300 * 100)) / 100.0 + 200.0;
    }

//    public static DataDAO getDemandData(List<DemandScheduleDAO> demands) {
//        DataDAO data = new DataDAO();
//        List<MeasurementDAOResponse> measurementDAOResponseStream = demands.stream().filter(
//                it -> it.getDemandDefinition().getTile() != null
//        )
//                .flatMap(demand -> demand.getDemandDefinition().getTile().getMeasurements().stream()).collect(
//                        Collectors.toList());
//        Map<String, Double> measurementValues = demands.stream().filter(
//                it -> it.getDemandDefinition().getTile() != null
//        )
//                .flatMap(demand -> demand.getDemandDefinition().getTile().getMeasurements().stream())
//                .collect(Collectors.toMap(it -> it.getId().toString(), DummyDataGenerator::getMeasurementValue,
//                        (a1, a2) -> a1));
//
//        data.getCurrent().setLast(measurementValues);
//
//        return data;
//    }

//    public static DataDAO getData(List<Measurement> measurements) {
//        DataDAO data = new DataDAO();
//        Map<String, Double> measurementValues = measurements.stream().collect(
//                Collectors.toMap(it -> it.getId().toString(), DummyDataGenerator::getMeasurementValue, (a1, a2) -> a1));
//        data.getCurrent().setLast(measurementValues);
//        return data;
//    }

    public static DataDAO getData(Collection<MeasurementDAOResponse> measurements) {
        DataDAO data = new DataDAO();
        Map<String, Double> measurementValues = measurements.stream().collect(
                Collectors.toMap(it -> it.getId().toString(), DummyDataGenerator::getMeasurementValue, (a1, a2) -> a1));
        data.getCurrent().put("last", measurementValues);
        return data;
    }

//    public static List<DemandScheduleDAO> getDemand(List<DemandDefinition> demands, List<Asset> assets) {
//        long current = (new Date()).getTime();
//
//        return demands.stream().flatMap(
//                def -> assets.stream().map(
//                        asset -> {
//                            DemandSchedule demandSchedule = new DemandSchedule();
//                            demandSchedule.setAsset(asset);
//                            demandSchedule.setDemandDefinition(def);
//                            demandSchedule.setDemandStart(DateConverter.toLocalDateTime(current - 3600000));
//                            demandSchedule.setDemandStop(DateConverter.toLocalDateTime(current + 3600000));
//                            return demandSchedule;
//                        }
//                )
//
//        ).map(DemandScheduleDAO::create).collect(Collectors.toList());
//    }

    public static List<DemandScheduleDAO> getDemand(List<DemandScheduleDAO> schedule) {
        long current = (new Date()).getTime();
        return schedule.stream().peek(s -> {
                    s.setDemandStart(current - 3600000);
                    s.setDemandStop(current + 3600000);
                }
        ).collect(Collectors.toList());
    }

    public static List<NotificationDAO> getNotifications() {
        float chance = 0.45f;
        ArrayList<NotificationDAO> l = new ArrayList<>();
        while (random.nextFloat() < chance) {
            var not = new NotificationDAO();
            switch (random.nextInt(3)) {
                case 0:
                    not.setType(NotificationType.anomaly);
                    break;
                case 1:
                    not.setType(NotificationType.warning);
                    break;
                case 2:
                    not.setType(NotificationType.information);
                    break;
                default:
                    not.setType(NotificationType.information);
                    break;
            }
            not.setMessage(s.substring(0, 19 + random.nextInt(s.length() - 20)));
//         not.setIcon();
            not.setDateFrom((new Date()).getTime() - 3600000);
            not.setDateTo((new Date()).getTime() + 3600000);
            //        assetId,dashboardId,informationTileId;
            l.add(not);
            chance*=0.7f;
        }
        return l;

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