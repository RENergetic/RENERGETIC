package com.renergetic.dataapi.service.utils;

import com.renergetic.common.dao.*;
import com.renergetic.common.model.*;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.common.repository.MeasurementTypeRepository;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.common.utilities.Json;
import com.renergetic.common.utilities.OffSetPaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class DummyDataGenerator {
    @Autowired
    MeasurementTypeRepository measurementTypeRepository;
    @Autowired
    MeasurementRepository measurementRepository;
    private static final Random random = new Random();
    static final String s =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce tellus nisl, finibus condimentum " +
                    "nisl vel, scelerisque venenatis ex. Vivamus pellentesque suscipit nunc id pulvinar. Aliquam vel" +
                    " hendrerit turpis. Orci varius natoque penatibus et magnis dis parturient montes, nascetur " +
                    "ridiculus mus. Aenean efficitur elementum mollis. Donec vel metus eu justo congue commodo";
    static final String NOTIFICATION_TEMPLATE = "test_prediction_template";

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
        Double max = 2000.0 /Math.pow(m.getType().getFactor(),0.6);
        if (m.getDomain() == Domain.heat) {
            max = 6000.0/Math.pow(m.getType().getFactor(),0.6);
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


    public DataDAO getData(Collection<MeasurementDAOResponse> measurements) {
        DataDAO data = new DataDAO();

        for (MeasurementDAOResponse it : measurements) {
            if (!data.getCurrent().containsKey(it.getFunction().name())) {
                data.getCurrent().put(it.getFunction().name(), new HashMap<>());
            }
            data.getCurrent().get(it.getFunction().name()).put(it.getId().toString(),
                    DummyDataGenerator.getMeasurementValue(it));
        }
        return data;
    }


    public TimeseriesDAO getTimeseries(Collection<MeasurementDAOResponse> measurements, Long from, Optional<Long> to) {
        TimeseriesDAO data = new TimeseriesDAO();
        Long dateTo = to.isPresent() ? to.get() : (new Date()).getTime();
        Long dateFrom = from != null ? from : dateTo - 3600L * 1000L * 24L; //24h
        int points = 500;
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
        if (l.isEmpty() || !random.nextBoolean()) {
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