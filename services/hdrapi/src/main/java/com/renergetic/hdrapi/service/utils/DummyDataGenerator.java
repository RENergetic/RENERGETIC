package com.renergetic.hdrapi.service.utils;

//import com.renergetic.common.dao.*;

import com.renergetic.common.dao.*;
import com.renergetic.common.dao.details.MeasurementTagsDAO;
import com.renergetic.common.model.*;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.common.repository.MeasurementTagsRepository;
import com.renergetic.common.repository.MeasurementTypeRepository;
import com.renergetic.common.repository.RecommendationRepository;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.common.utilities.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DummyDataGenerator {
    @Autowired
    MeasurementTypeRepository measurementTypeRepository;
    @Autowired
    MeasurementRepository measurementRepository;
    @Autowired
    RecommendationRepository recommendationRepository;
    @Autowired
    MeasurementTagsRepository measurementTagsRepository;
    private static final Random random = new Random();
    static final String s =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce tellus nisl, finibus condimentum " +
                    "nisl vel, scelerisque venenatis ex. Vivamus pellentesque suscipit nunc id pulvinar. Aliquam vel" +
                    " hendrerit turpis. Orci varius natoque penatibus et magnis dis parturient montes, nascetur " +
                    "ridiculus mus. Aenean efficitur elementum mollis. Donec vel metus eu justo congue commodo";
    static final String notificationTemplate = "test_prediction_template";

    private static Double getMeasurementValue(Measurement m) {
        return getMeasurementValue(m, null);
    }

    private static Double getMeasurementValue(Measurement m, Double previousValue) {
        Double max = 30000.0;
        if (m.getDomain() == Domain.heat) {
            max = 50000.0;
        }
        if (Objects.equals(m.getType().getPhysicalName(), "percentage")) {
            max = 100.0;
        }
//TODO: consider measurement type and domains
        if (previousValue == null)
            return (random.nextInt(max.intValue() * 100)) / 100.0 + max * 0.66;
        else {
            return previousValue + (random.nextInt((max.intValue() / 3) * 100)) / 100.0 - max * 0.1;
        }
    }

    private static Double getMeasurementValue(MeasurementDAOResponse m) {
        return getMeasurementValue(m, null);
    }

    private static Double getMeasurementValue(MeasurementDAOResponse m, Double previousValue) {
//TODO: consider measurement type and domains
        Double max = 300.0 / Math.pow(m.getType().getFactor(), 0.6);
        if (m.getDomain() == Domain.heat) {
            max = 5000.0 / Math.pow(m.getType().getFactor(), 0.6);
        }

        if (Objects.equals(m.getType().getPhysicalName(), "percentage")) {
            max = 100.0;
        }
//TODO: consider measurement type and domains
        if (previousValue == null)
            return (random.nextInt(max.intValue() * 100)) / 100.0 + max * 0.66;
        else {
            return previousValue + (random.nextInt((max.intValue() / 3) * 100)) / 100.0 - max * 0.1;
        }
//        if (previousValue == null)
//            return (random.nextInt(300 * 100)) / 100.0 + 200.0;
//        else {
//            return previousValue+(random.nextInt(40 * 100)) / 100.0 -15.0;
//        }
    }


    public DataDAO getData(Collection<MeasurementDAOResponse> measurements) {
        DataDAO data = new DataDAO();

//        Map<String, Double> measurementValues = measurements.stream().collect(
//                Collectors.toMap(it -> it.getId().toString(), DummyDataGenerator::getMeasurementValue, (a1, a2) -> a1));

        for (MeasurementDAOResponse it : measurements) {
            if (!data.getCurrent().containsKey(it.getFunction().name())) {
                data.getCurrent().put(it.getFunction().name(), new HashMap<>());
            }
            data.getCurrent().get(it.getFunction().name()).put(it.getId().toString(),
                    DummyDataGenerator.getMeasurementValue(it));
        }

//        data.getCurrent().put("last", measurementValues);
        return data;
    }


    public TimeseriesDAO getTimeseries(Collection<MeasurementDAOResponse> measurements, Long from, Optional<Long> to) {
        TimeseriesDAO data = new TimeseriesDAO();
        Long dateTo = to.isPresent() ? to.get() : (new Date()).getTime();
        Long dateFrom = from != null ? from : dateTo - 3600L * 1000L * 24L; //24h
//        Long interval = 300L * 1000L;//  5 min interval, 6 points per hour
        int points = 500;//24 * 6;
        long interval = (dateTo - dateFrom) / 500;
        Long[] timestamps = new Long[points];
        for (int i = 0; i < timestamps.length; i++) {
            if (i > points * 0.6)
                timestamps[i] = dateFrom + interval * i;
            else {
                timestamps[i] = dateFrom + interval / 10 * i;
            }
        }
        data.setTimestamps(Arrays.asList(timestamps));
        Map<String, List<Double>> map = new HashMap<>();

        ArrayList<MeasurementDAOResponse> tList = new ArrayList<>(measurements);
        for (int j = 0; j < tList.size(); j++) {
            Double[] values = new Double[points];
            var m = tList.get(j);
            for (int i = 0; i < values.length; i++) {
                values[i] = getMeasurementValue(m, i > 0 ? values[i - 1] : null);
            }

            map.put(m.getId().toString(), Arrays.asList(values));
        }
        data.setCurrent(map);

        return data;
    }

    public List<HDRRecommendationDAO> getRecommendations() {
        var t = DateConverter.toEpoch(LocalDateTime.now()) / 1000L / 60L;
        final long timestamp = t * 60 * 1000;
        var tags = measurementTagsRepository.findAll().stream().filter(
                (it) -> it.getKey().equals("recommendation")).collect(
                Collectors.toList());

        var recommendations = IntStream.range(0, tags.size()).mapToObj((idx) -> {
                    var it = tags.get(idx);
                    var hdr = new HDRRecommendationDAO();
                    hdr.setTimestamp(timestamp);
                    hdr.setTag(MeasurementTagsDAO.create(it));
                    if(random.nextBoolean()){
                        hdr.setLabel("Decrease temperature by " + random.nextInt());
                    }
                    else{
                        hdr.setLabel("Increase temperature by " + random.nextInt());
                    }

                    hdr.setId((long) idx);
                    return hdr;
                }).filter((it) -> !it.getTag().getValue().equals("no_tag"))
                .filter((it) -> random.nextBoolean() || random.nextBoolean()).collect(Collectors.toList());
        return recommendations;

    }


    public List<MeasurementDAOResponse> getMeasurements(Long id) {
        var r = recommendationRepository.findById(id);
        long tagId;
        if (r.isEmpty()) {
            var tag = measurementTagsRepository.findAll().stream().filter(
                    (it) -> it.getKey().equals("recommendation")).collect(
                    Collectors.toList()).get(id.intValue());
            tagId = tag.getId();
        } else {
            tagId = r.get().getTag().getId();
        }
//        MeasurementTags tags = measurementTagsRepository.findById(tagId).orElseThrow(
//                () -> new NotFoundException("Tag with id: " + tagId + " not exists"));
//        tagId=tags.getId();
        List<Measurement> measurements = measurementRepository.getMeasurementByTagId(tagId, 0L, 100L);
        return measurements.stream().map(it -> MeasurementDAOResponse.create(it, null, null)).collect(
                Collectors.toList());

    }

    public static List<DemandScheduleDAO> getDemand(List<DemandScheduleDAO> schedule) {
        long current = (new Date()).getTime();
        return schedule.stream().peek(s -> {
                    s.setDemandStart(current - 3600000);
                    s.setDemandStop(current + 3600000);
                }
        ).collect(Collectors.toList());
    }

    public List<NotificationScheduleDAO> getNotifications() {
        float chance = 0.75f;
        ArrayList<NotificationScheduleDAO> l = new ArrayList<>();
        while (random.nextFloat() < chance) {
            var not = new NotificationScheduleDAO();
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
            if (random.nextInt() % 2 == 0) {
                not.setDashboard(initDashboard(l.size()));
            }
            if (random.nextBoolean()) {
                Page<Measurement> ml = measurementRepository.findAll(new OffSetPaging(0, 100));
                int idx = random.nextInt(ml.getContent().size());
                Measurement measurement = ml.getContent().get(idx);
                not.setMeasurement(MeasurementDAOResponse.create(measurement, null, null));
                not.setAsset(SimpleAssetDAO.create(measurement.getAsset()));
                not.setValue(getMeasurementValue(measurement));
                Date dt = new Date((new Date()).getTime() + (3600 * 1000));
                not.setNotificationTimestamp(DateConverter.toEpoch(dt));
                not.setMessage("test_prediction_template");
            }

            //        assetId,dashboardId,informationTileId;
            l.add(not);
            chance *= 0.9f;
        }
        return l;

    }

    private DashboardDAO initDashboard(int n) {
        Dashboard d = new Dashboard("dashboard_" + n, "http://www.example.org/" + n, "Sample, test dashboard " + n);
        d.setId((long) n);
        d.setGrafanaId("grafanaid_" + n);
        List<MeasurementType> l = this.measurementTypeRepository.findByDashboardVisibility();
        Map<String, ? extends Serializable> ext;
        if (l.size() == 0 || !random.nextBoolean()) {
            ext = Map.of("model", random.nextBoolean() ? "model_" + n : "",
                    "measurement_type", "", "unit", "");
        } else {
            MeasurementType type = l.get(random.nextInt(l.size()));
            ext = Map.of("model", random.nextBoolean() ? "model_" + n : "",
                    "measurement_type", type.getPhysicalName(), "unit", type.getUnit());
        }

        d.setExt(Json.toJson(ext));
        return DashboardDAO.create(d);
    }


    public List<DashboardDAO> getDashboards(int n) {
        List<DashboardDAO> l = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            l.add(i, initDashboard(i));
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
 *///    public static List<DemandScheduleDAO> getDemand(List<DemandDefinition> demands, List<Asset> assets) {
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