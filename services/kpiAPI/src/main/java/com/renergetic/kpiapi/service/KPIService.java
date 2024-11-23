package com.renergetic.kpiapi.service;

import com.renergetic.common.dao.MeasurementDAO;
import com.renergetic.common.model.Domain;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.common.utilities.HttpAPIs;
import com.renergetic.kpiapi.dao.DataWrapperDAO;
import com.renergetic.kpiapi.dao.KPIDataDAO;
import com.renergetic.kpiapi.dao.MeasurementDAORequest;
import com.renergetic.kpiapi.exception.HttpRuntimeException;
import com.renergetic.kpiapi.exception.InvalidArgumentException;
import com.renergetic.kpiapi.model.*;
import com.renergetic.kpiapi.repository.KPIConstantRepository;

import com.renergetic.kpiapi.service.kpi.*;
import com.renergetic.kpiapi.service.utils.MathCalculator;

import com.renergetic.kpiapi.service.utils.MeterTimespan;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
public class KPIService {
    final Map<String, String> influxHeaders = Map.of("Content-Type", "application/json");
    @Value("${influx.api.url}")
    private String influxURL;

    @Autowired
    private HttpAPIs httpAPIs;

    @Autowired
    private KPIConstantRepository constantRepository;

    @Value("${scheduled.calculation.period}")
    private Integer meterPeriod;
    @Value("${scheduled.kpi.frequency}")
    private Integer meterFrequency;
    @Autowired
    private MathCalculator calculator;
    @Autowired
    MeasurementRepository measurementRepository;

    @PostConstruct
    private void setRepositories() {
        CO2.Instance.setConstantRepository(this.constantRepository);
    }

    //Arrays.stream(KPI.values()).toList()
    public List<KPIDataDAO> calculateAndInsertAll(Domain domain, MeterTimespan ts) {
        List<KPIDataDAO> configuredMeters = new LinkedList<>();
        var calculated = calculateAllKPIMeters(domain, ts);
        // Calculate and save each KPI
        for (var entry : calculated.entrySet()) {
            var kpi = entry.getKey();
            var value = entry.getValue();
            try {

                MeasurementDAORequest influxRequest = MeasurementDAORequest.create(kpi, domain);
                //some fields aren't optional because there would be no sense to mix them -> can be discussed
                MeasurementDAO m = null;
                try {
                    m = measurementRepository.findMeasurements(null, null, kpi.name().toLowerCase(), "kpi", domain.name(), null, null, null, 0, 1).get(0);
                } catch (Exception ex) {
                    log.error("KPI missing measurement definition: " + kpi.kpi + " for domain: " + domain.name() + " measurement not found");
                }

                if (m != null) {
                    influxRequest.getFields().put(m.getTypeName(), value);
                } else {
                    //            hotfix
                    if (kpi.typeName.equals("energy")) {
                        //TODO: horfix
                        //https://renergetic-renergetic-wp5.apps.paas-dev.psnc.pl/api-base/1.0/api/measurements/report?type_physical_name=energy&domain=heat&sensor_name=kpi&offset=0&limit=25
                        influxRequest.getFields().put("energy_kwh", value);
                    } else influxRequest.getFields().put(kpi.typeName, value);
                }

                influxRequest.getFields().put("time", DateConverter.toString(ts.getTsTo()));
                HttpResponse<String> response = httpAPIs.sendRequest(influxURL + "/api/measurement", "POST",
                        null, influxRequest, influxHeaders);
                if (response != null && response.statusCode() < 300) {
                    KPIDataDAO data = KPIDataDAO.create(kpi, domain);
                    data.getData().put(ts.getTsTo(), Double.parseDouble(value));
                    configuredMeters.add(data);
                } else if (response != null)
                    log.error(String.format("Error saving data in Influx for KPI %s with domain %s: %s", kpi.kpi, domain.toString(), response.statusCode()));
                else
                    log.error(String.format("Error retrieving data from Influx for KPI %s with domain %s: NULL response", kpi.kpi, domain.toString()));
            } catch (Exception e) {
                log.error("Error calculating KPI: " + kpi.kpi + " for domain: " + domain.name(), e);
            }
        }
        return configuredMeters;
    }

    public DataWrapperDAO calculateAllKPIs(Domain domain, Long ts) {
        var span = MeterTimespan.init(meterPeriod * meterFrequency, ts);
        var data = this.calculateAllKPIMeters(domain, span).entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().kpi, Map.Entry::getValue));
        return new DataWrapperDAO(data, domain.name(), span);
    }

    public HashMap<KPI, String> calculateAllKPIMeters(Domain domain, MeterTimespan span) {
        Set<Thread> threads = new HashSet<>();
        HashMap<KPI, String> calculatedKPIs = new HashMap<>();
        AbstractMeterDataWrapper dataWrapper = new AbstractMeterDataWrapper();
        dataWrapper.values.forEach((key, value) -> threads.add(new Thread(() ->
                dataWrapper.values.put(key, this.getAbstractMeterData(key, domain, span.getTsFrom(), span.getTsTo(), InfluxFunction.SUM)))));

        dataWrapper.previousValues.forEach((key, value) -> threads.add(new Thread(() ->
                dataWrapper.previousValues.put(key, this.getAbstractMeterData(key, domain, span.getTsFrom() - (span.getTsTo() - span.getTsFrom()), span.getTsFrom(), InfluxFunction.SUM)))));

        dataWrapper.maxValues.forEach((key, value) -> threads.add(new Thread(() ->
                dataWrapper.maxValues.put(key, this.getAbstractMeterData(key, domain, span.getTsFrom(), span.getTsTo(), InfluxFunction.MAX)))));

        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                thread.interrupt();
                e.printStackTrace();
            }
        });
        var kpis = Arrays.stream(KPI.values()).toList();
        if (domain == Domain.none) {
            kpis = Stream.of(ESS.Instance, ESC.Instance, EP.Instance).map(KPIFormula::getKPI).toList();
        }
        // Calculate and save each KPI
        for (KPI kpi : kpis) {
            try {
                BigDecimal value = calculateKPI(kpi, domain, span.getTsFrom(), span.getTsTo(),
                        dataWrapper.values, dataWrapper.previousValues, dataWrapper.maxValues);
                log.info("Calculate: " + kpi.kpi + " for: " + domain.name() + " = " + value.toString());//Start Calculate:

                //some fields aren't optional because there would be no sense to mix them -> can be discussed
                //TODO: not sure if user id and connection is required here - unless we want to check here if the user can view the data/strcuture of the panel

                MeasurementDAO m = null;
                try {
                    m = measurementRepository.findMeasurements(null, null, kpi.name().toLowerCase(), "kpi", domain.name(), null, null, null, 0, 1).get(0);

                } catch (Exception ex) {
                    log.error("Error calculating KPI: " + kpi.kpi + " for domain: " + domain.name() + " measurement not found");
                }
                var kpiValue = calculator.bigDecimalToDoubleString(value);
                //kpi. TODO: link KPI with measurements
                calculatedKPIs.put(kpi, kpiValue);

            } catch (Exception e) {
                log.error("Error calculating KPI: " + kpi.kpi + " for domain: " + domain.name(), e);
            }
        }
        return calculatedKPIs;
    }


    private BigDecimal calculateKPI(KPI kpi, Domain domain, Long from, Long to, Map<AbstractMeter, Double> values,
                                    Map<AbstractMeter, Double> previousValues, Map<AbstractMeter, Double> maxValues) {

        // Calculate each KPI with the values retrieved before
        return switch (kpi) {
            case ESS -> ESS.Instance.calculate(values);//this.calculateESS(values);
            case EP -> EP.Instance.calculate(values);//this.calculateEP(values);
            case EE -> EE.Instance.calculate(values);//this.calculateEE(values);
            case ES -> ES.Instance.calculate(values, previousValues);//this.calculateES(values, previousValues);
            case SRES -> SRES.Instance.calculate(values); //this.calculateSRES(values);
            case SNES -> SNES.Instance.calculate(values);//this.calculateSNES(values);
            case CO2 -> CO2.Instance.calculate(values); //this.calculateCO2(values);
            case PEAK -> PEAK.Instance.calculate(maxValues);// this.calculatePEAK(maxValues);
            case ESC -> ESC.Instance.calculate(values); // this.calculateESC(values);
        };
    }

    private Double getAbstractMeterData(AbstractMeter meter, Domain domain, Long from, Long to,
                                        InfluxFunction operation) {
        Map<String, String> params = new HashMap<>();

        // Set parameters to Influx API request
        params.put("measurements", "abstract_meter");
        params.put("measurement_type", meter.name().toLowerCase());
        params.put("domain", domain.name());
        if (from != null) params.put("from", from.toString());
        if (to != null) params.put("to", to.toString());

        // Send request to Influx API
        HttpResponse<String> response = httpAPIs.sendRequest(influxURL + "/api/measurement/data/" + operation.name().toLowerCase(), "GET", params, null, null);

        // Parse response with status code smaller than 300
        if (response != null && response.statusCode() < 300) {
            JSONArray data = new JSONArray(response.body());

            if (!data.isEmpty()) {
                for (Object obj : data) {
                    if (obj instanceof JSONObject) {
                        JSONObject json = ((JSONObject) obj).getJSONObject("fields");

                        if (json.has(operation.name().toLowerCase())) {
                            return json.getDouble(operation.name().toLowerCase());
                        }
                    }
                }
            }
        }
        return 0.;
    }

    private static class AbstractMeterDataWrapper {
        public final Map<AbstractMeter, Double> values = new EnumMap<>(AbstractMeter.class);
        public final Map<AbstractMeter, Double> previousValues = new EnumMap<>(AbstractMeter.class);
        public final Map<AbstractMeter, Double> maxValues = new EnumMap<>(AbstractMeter.class);

        public AbstractMeterDataWrapper() {

            values.put(AbstractMeter.LOAD, 0.);
            values.put(AbstractMeter.LOSSES, 0.);
            values.put(AbstractMeter.STORAGE, 0.);
            values.put(AbstractMeter.ENS, 0.);
            values.put(AbstractMeter.ERS, 0.);
            values.put(AbstractMeter.EXCESS, 0.);
            values.put(AbstractMeter.RES, 0.);
            values.put(AbstractMeter.LNS, 0.);
            values.put(AbstractMeter.LRS, 0.);

            previousValues.put(AbstractMeter.LOAD, 0.);
            previousValues.put(AbstractMeter.LOSSES, 0.);
            previousValues.put(AbstractMeter.STORAGE, 0.);

            maxValues.put(AbstractMeter.LOAD, 0.);
            maxValues.put(AbstractMeter.LOSSES, 0.);
            maxValues.put(AbstractMeter.STORAGE, 0.);

        }
    }

    //region old

    /**
     * Returns an KPIDataDAO object after performing an
     * Influx API request for aggregated data.
     *
     * @param name      the name of the measurement
     * @param domain    the domain of the measurement
     * @param operation the operation to perform on the measurement data
     * @param from      the starting timestamp of the data to retrieve (optional)
     * @param to        the ending timestamp of the data to retrieve (optional)
     * @param group     the group parameter to be sent to Influx API (optional)
     * @return an KPIDataDAO object containing the retrieved data
     */
    public KPIDataDAO getAggregated(String name, Domain domain, InfluxFunction operation, Long from, Long to,
                                    String group) {

        KPIDataDAO ret = new KPIDataDAO();

        ret.setName(KPI.obtain(name));
        ret.setDomain(domain);

        Map<String, String> params = new HashMap<>();

        // Set parameters to Influx API request
        params.put("measurements", "kpi");
        params.put("measurement_type", ret.getName().name().toLowerCase());
        params.put("domain", domain.name());
        if (from != null) params.put("from", from.toString());
        if (to != null) params.put("to", to.toString());
        if (group != null) params.put("group", group);

        // Send request to Influx API
        HttpResponse<String> response = httpAPIs.sendRequest(influxURL + "/api/measurement/data/" + operation.name().toLowerCase(), "GET", params, null, null);

        // Parse response with status code smaller than 300
        if (response != null && response.statusCode() < 300) {
            JSONArray data = new JSONArray(response.body());

            if (!data.isEmpty()) {
                data.forEach(obj -> {
                    if (obj instanceof JSONObject) {
                        JSONObject json = ((JSONObject) obj).getJSONObject("fields");

                        Long timestamp = null;
                        try {
                            if (json.has("time") && !json.isNull("time")) {
                                timestamp = DateConverter.toEpoch(json.getString("time"));
                            }
                        } catch (InvalidArgumentException e) {
                            log.warn(e.getMessage());
                        }
                        if (ret.getUnit() != null) {
                            ret.getData().put(timestamp, json.getDouble(ret.getUnit().getName()));
                        } else {
                            if (json.has("value")) {
                                ret.getData().put(timestamp, json.getDouble("value"));
                            }
                        }
                    }
                });
            }
        } else if (response != null)
            throw new HttpRuntimeException(String.format("Error retrieving data from Influx for KPI %s with domain %s: %s", ret.getName(), domain.toString(), response.statusCode()));
        else
            throw new HttpRuntimeException(String.format("Error retrieving data from Influx for KPI %s with domain %s: NULL response", ret.getName(), domain.toString()));

        return ret;
    }


    /**
     * Retrieves the kpi data DAO for the given name, domain, and time range.
     *
     * @param name   the name of the meter data
     * @param domain the domain of the meter data
     * @param from   the start time of the meter data (null for all time)
     * @param to     the end time of the meter data (null for all time)
     * @return the KPIDataDAO object containing the requested data
     */
    public KPIDataDAO get(String name, Domain domain, Long from, Long to) {

        KPIDataDAO ret = new KPIDataDAO();

        ret.setName(KPI.obtain(name));
        ret.setDomain(domain);

        Map<String, String> params = new HashMap<>();

        // Set parameters to Influx API request
        params.put("measurements", "kpi");
        params.put("measurement_type", ret.getName().name().toLowerCase());
        params.put("domain", domain.name());
        if (from != null) params.put("from", from.toString());
        if (to != null) params.put("to", to.toString());

        // Send request to Influx API
        HttpResponse<String> response = httpAPIs.sendRequest(influxURL + "/api/measurement/data", "GET", params, null, null);

        // Parse response with status code smaller than 300
        if (response != null && response.statusCode() < 300) {
            JSONArray data = new JSONArray(response.body());

            if (!data.isEmpty()) {
                data.forEach((obj) -> {
                    if (obj instanceof JSONObject) {
                        JSONObject json = ((JSONObject) obj).getJSONObject("fields");

                        Long timestamp = DateConverter.toEpoch(json.getString("time"));
                        if (ret.getUnit() != null) {
                            ret.getData().put(timestamp, json.getDouble(ret.getUnit().getName()));
                        } else {
                            if (json.has("value")) {
                                ret.getData().put(timestamp, json.getDouble("value"));
                            }
                        }
                    }
                });
            }
        } else if (response != null)
            throw new HttpRuntimeException(String.format("Error retrieving data from Influx for KPI %s with domain %s: %s", ret.getName(), domain.toString(), response.statusCode()));
        else
            throw new HttpRuntimeException(String.format("Error retrieving data from Influx for KPI %s with domain %s: NULL response", ret.getName(), domain.toString()));

        return ret;
    }


//    public List<KPIDataDAO> calculateAndInsertAll(Domain domain, Long from, Long to, long time) {
//
//        // Prepare Abstract meter needed values
//        Set<Thread> threads = new HashSet<>();
//
//        Map<AbstractMeter, Double> values = new EnumMap<>(AbstractMeter.class);
//        Map<AbstractMeter, Double> previousValues = new EnumMap<>(AbstractMeter.class);
//        Map<AbstractMeter, Double> maxValues = new EnumMap<>(AbstractMeter.class);
//
//        values.put(AbstractMeter.LOAD, 0.);
//        values.put(AbstractMeter.LOSSES, 0.);
//        values.put(AbstractMeter.STORAGE, 0.);
//        values.put(AbstractMeter.ENS, 0.);
//        values.put(AbstractMeter.ERS, 0.);
//        values.put(AbstractMeter.EXCESS, 0.);
//        values.put(AbstractMeter.RES, 0.);
//        values.put(AbstractMeter.LNS, 0.);
//        values.put(AbstractMeter.LRS, 0.);
//
//        previousValues.put(AbstractMeter.LOAD, 0.);
//        previousValues.put(AbstractMeter.LOSSES, 0.);
//        previousValues.put(AbstractMeter.STORAGE, 0.);
//
//        maxValues.put(AbstractMeter.LOAD, 0.);
//        maxValues.put(AbstractMeter.LOSSES, 0.);
//        maxValues.put(AbstractMeter.STORAGE, 0.);
//
//        values.forEach((key, value) -> threads.add(new Thread(() -> values.put(key, this.getAbstractMeterData(key, domain, from, to, InfluxFunction.SUM)))));
//
//        previousValues.forEach((key, value) -> threads.add(new Thread(() -> previousValues.put(key, this.getAbstractMeterData(key, domain, from - (to - from), from, InfluxFunction.SUM)))));
//
//        maxValues.forEach((key, value) -> threads.add(new Thread(() -> maxValues.put(key, this.getAbstractMeterData(key, domain, from, to, InfluxFunction.MAX)))));
//
//        threads.forEach(Thread::start);
//
//        // Wait to retrieve all data
//        threads.forEach(thread -> {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                thread.interrupt();
//                e.printStackTrace();
//            }
//        });
//
//        Map<String, String> headers = Map.of("Content-Type", "application/json");
//
//        List<KPIDataDAO> configuredMeters = new LinkedList<>();
//
//        // Calculate and save each KPI
//        for (KPI kpi : KPI.values()) {
//            try {
//                log.info("Start Calculate: " + kpi.kpi + " for: " + domain.name());
//                MeasurementDAORequest influxRequest = MeasurementDAORequest.create(kpi, domain);
//
////                if (time != null)
////                    influxRequest.getFields().put("time", DateConverter.toString(time));
//
//                BigDecimal value = calculateKPI(kpi, domain, from, to, values, previousValues, maxValues);
//
//                //some fields aren't optional because there would be no sense to mix them -> can be discussed
//                //TODO: not sure if user id and connection is required here - unless we want to check here if the user can view the data/strcuture of the panel
//
//                MeasurementDAO m = null;
//                try {
//                    m = measurementRepository.findMeasurements(null, null, kpi.name().toLowerCase(), "kpi", domain.name(), null, null, null, 0, 1).get(0);
//
//                } catch (Exception ex) {
//                    log.error("Error calculating KPI: " + kpi.kpi + " for domain: " + domain.name() + " measurement not found");
//                }
//                //kpi. TODO: link KPI with measurements
//
//                if (m != null) {
//                    influxRequest.getFields().put(m.getTypeName(), calculator.bigDecimalToDoubleString(value));
//                } else {
//                    //            hotfix
//                    if (kpi.typeName.equals("energy")) {
//                        //TODO: horfix
//                        //https://renergetic-renergetic-wp5.apps.paas-dev.psnc.pl/api-base/1.0/api/measurements/report?type_physical_name=energy&domain=heat&sensor_name=kpi&offset=0&limit=25
//                        influxRequest.getFields().put("energy_kwh", calculator.bigDecimalToDoubleString(value));
//                    } else influxRequest.getFields().put(kpi.typeName, calculator.bigDecimalToDoubleString(value));
//
//                }
//
//                influxRequest.getFields().put("time", DateConverter.toString(time));
//                HttpResponse<String> response = httpAPIs.sendRequest(influxURL + "/api/measurement", "POST", null, influxRequest, headers);
//
//                if (response != null && response.statusCode() < 300) {
//                    KPIDataDAO data = KPIDataDAO.create(kpi, domain);
//                    data.getData().put(time, value.doubleValue());
//                    configuredMeters.add(data);
//                } else if (response != null)
//                    log.error(String.format("Error saving data in Influx for KPI %s with domain %s: %s", kpi.kpi, domain.toString(), response.statusCode()));
//                else
//                    log.error(String.format("Error retrieving data from Influx for KPI %s with domain %s: NULL response", kpi.kpi, domain.toString()));
//            } catch (Exception e) {
//                log.error("Error calculating KPI: " + kpi.kpi + " for domain: " + domain.name(), e);
//            }
//        }
//        return configuredMeters;
//    }
//    public List<KPIDataDAO> calculateAndInsert(Domain domain, List<KPIFormula> kpis, Long from, Long to, Long time) {
//
//        AbstractMeterKPIConfig[] meters = KPIFormula.getRequiredAbstractMeters(kpis);
//        // Prepare Abstract meter needed values
//        Set<Thread> threads = new HashSet<>();
//
//        Map<AbstractMeter, Double> values = Arrays.stream(meters).filter(it -> it.getPeriod() == 0 && it.getFunction() == InfluxFunction.SUM).collect(Collectors.toMap(AbstractMeterKPIConfig::getAbstractMeter, it -> 0.0));// new EnumMap<>(AbstractMeter.class);
//        Map<AbstractMeter, Double> previousValues = Arrays.stream(meters).filter(it -> it.getPeriod() == -1 && it.getFunction() == InfluxFunction.SUM).collect(Collectors.toMap(AbstractMeterKPIConfig::getAbstractMeter, it -> 0.0));// new EnumMap<>(AbstractMeter.class);
//        Map<AbstractMeter, Double> maxValues = Arrays.stream(meters).filter(it -> it.getPeriod() == 0 && it.getFunction() == InfluxFunction.MAX).collect(Collectors.toMap(AbstractMeterKPIConfig::getAbstractMeter, it -> 0.0));// new EnumMap<>(AbstractMeter.class);
//
//
//        values.forEach((key, value) -> threads.add(new Thread(() -> values.put(key, this.getAbstractMeterData(key, domain, from, to, InfluxFunction.SUM)))));
//
//        previousValues.forEach((key, value) -> threads.add(new Thread(() -> previousValues.put(key, this.getAbstractMeterData(key, domain, from - (to - from), from, InfluxFunction.SUM)))));
//
//        maxValues.forEach((key, value) -> threads.add(new Thread(() -> maxValues.put(key, this.getAbstractMeterData(key, domain, from, to, InfluxFunction.MAX)))));
//
//        threads.forEach(Thread::start);
//
//        // Wait to retrieve all data
//        threads.forEach(thread -> {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                thread.interrupt();
//                e.printStackTrace();
//            }
//        });
////        Map<String,Double> metersValues = new HashMap<>(); TODO: map values with AbstractMeterKPIConfig.getKey()
////       values.forEach();
//        Map<String, String> headers = Map.of("Content-Type", "application/json");
//
//        List<KPIDataDAO> configuredMeters = new LinkedList<>();
//
//        // Calculate and save each KPI
//        for (var kpiFormula : kpis) {
//            var kpi = kpiFormula.getKPI();
//            log.info("Start Calculate: " + kpi.kpi + " for: " + domain.name());
//            MeasurementDAORequest influxRequest = MeasurementDAORequest.create(kpi, domain);
//
//            influxRequest.getFields().put("time", DateConverter.toString(time));
//
//            BigDecimal value = calculateKPI(kpi, domain, from, to, values, previousValues, maxValues);
//
//            influxRequest.getFields().put(kpi.typeName, calculator.bigDecimalToDoubleString(value));
//
//            HttpResponse<String> response = httpAPIs.sendRequest(influxURL + "/api/measurement", "POST", null, influxRequest, headers);
//
//            if (response != null && response.statusCode() < 300) {
//                KPIDataDAO data = KPIDataDAO.create(kpi, domain);
//                data.getData().put(time, value.doubleValue());
//                configuredMeters.add(data);
//            } else if (response != null)
//                log.error(String.format("Error saving data in Influx for KPI %s with domain %s: %s", kpi.kpi, domain.toString(), response.statusCode()));
//            else
//                log.error(String.format("Error retrieving data from Influx for KPI %s with domain %s: NULL response", kpi.kpi, domain.toString()));
//        }
//        return configuredMeters;
//    }

    //endregion
}

