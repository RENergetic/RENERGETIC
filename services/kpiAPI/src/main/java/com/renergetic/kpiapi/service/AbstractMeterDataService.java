package com.renergetic.kpiapi.service;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.*;

import com.renergetic.common.model.Domain;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.common.utilities.HttpAPIs;
import com.renergetic.kpiapi.dao.DataWrapperDAO;
import com.renergetic.kpiapi.service.utils.MeterTimespan;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.renergetic.kpiapi.dao.MeasurementDAORequest;
import com.renergetic.kpiapi.dao.AbstractMeterDataDAO;
import com.renergetic.kpiapi.exception.HttpRuntimeException;
import com.renergetic.kpiapi.exception.InvalidArgumentException;
import com.renergetic.kpiapi.exception.NotFoundException;
import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.AbstractMeterConfig;
import com.renergetic.kpiapi.model.InfluxFunction;
import com.renergetic.kpiapi.repository.AbstractMeterRepository;
import com.renergetic.kpiapi.service.utils.MathCalculator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AbstractMeterDataService {

    @Value("${influx.api.url}")
    private String influxURL;
    @Value("${scheduled.calculation.period}")
    private Integer meterPeriod;

    @Autowired
    private AbstractMeterRepository abstractMeterRepository;

    @Autowired
    private HttpAPIs httpAPIs;

    @Autowired
    private MathCalculator calculator;

    /**
     * Retrieves the meter data DAO for the given name, domain, and time range.
     *
     * @param name   the name of the meter data
     * @param domain the domain of the meter data
     * @param from   the start time of the meter data (null for all time)
     * @param to     the end time of the meter data (null for all time)
     * @return the AbstractMeterDataDAO object containing the requested data
     */
    public AbstractMeterDataDAO get(String name, Domain domain, Long from, Long to) {

        AbstractMeterDataDAO ret = new AbstractMeterDataDAO();

        ret.setName(AbstractMeter.obtain(name));
        ret.setDomain(domain);

        Map<String, String> params = new HashMap<>();

        // Set parameters to Influx API request
        params.put("measurements", "abstract_meter");
        params.put("measurement_type", ret.getName().name().toLowerCase());
        params.put("domain", domain.name());
        if (from != null)
            params.put("from", from.toString());
        if (to != null)
            params.put("to", to.toString());

        // Send request to Influx API
        HttpResponse<String> response =
                httpAPIs.sendRequest(influxURL + "/api/measurement/data", "GET", params, null, null);

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
            throw new HttpRuntimeException(String.format("Error retrieving data from Influx for Abtract meter %s with domain %s: %s", ret.getName(), domain.toString(), response.statusCode()));
        else
            throw new HttpRuntimeException(String.format("Error retrieving data from Influx for Abtract meter %s with domain %s: NULL response", ret.getName(), domain.toString()));

        return ret;
    }

    /**
     * Returns an AbstractMeterDataDAO object after performing an
     * Influx API request for aggregated data.
     *
     * @param name      the name of the measurement
     * @param domain    the domain of the measurement
     * @param operation the operation to perform on the measurement data
     * @param from      the starting timestamp of the data to retrieve (optional)
     * @param to        the ending timestamp of the data to retrieve (optional)
     * @param group     the group parameter to be sent to Influx API (optional)
     * @return an AbstractMeterDataDAO object containing the retrieved data
     */
    public AbstractMeterDataDAO getAggregated(String name, Domain domain, InfluxFunction operation, Long from, Long to,
                                              String group) {

        AbstractMeterDataDAO ret = new AbstractMeterDataDAO();

        ret.setName(AbstractMeter.obtain(name));
        ret.setDomain(domain);

        Map<String, String> params = new HashMap<>();

        // Set parameters to Influx API request
        params.put("measurements", "abstract_meter");
        params.put("measurement_type", ret.getName().name().toLowerCase());
        params.put("domain", domain.name());
        if (from != null)
            params.put("from", from.toString());
        if (to != null)
            params.put("to", to.toString());
        if (group != null)
            params.put("group", group);

        // Send request to Influx API
        HttpResponse<String> response =
                httpAPIs.sendRequest(influxURL + "/api/measurement/data/" + operation.name().toLowerCase(), "GET", params, null, null);

        // Parse response with status code smaller than 300
        if (response != null && response.statusCode() < 300) {
            JSONArray data = new JSONArray(response.body());

            if (!data.isEmpty()) {
                data.forEach((obj) -> {
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
            throw new HttpRuntimeException(String.format("Error retrieving data from Influx for Abtract meter %s with domain %s: %s", ret.getName(), domain.toString(), response.statusCode()));
        else
            throw new HttpRuntimeException(String.format("Error retrieving data from Influx for Abtract meter %s with domain %s: NULL response", ret.getName(), domain.toString()));

        return ret;
    }

//    public List<AbstractMeterDataDAO> calculateAll(Long ts) {
//        var span = MeterTimespan.init(meterPeriod, ts);
//        return this.calculateAll(span);
//    }

    public List<AbstractMeterDataDAO> calculateAndInsertAll(MeterTimespan ts) {
        log.info("Calc Abstract meter, from: " + ts.getTsFrom() + " to: " + ts.getTsTo());
        Map<String, String> headers = Map.of("Content-Type", "application/json");
        List<AbstractMeterDataDAO> configuredMeters = new LinkedList<>();
        List<AbstractMeterConfig> meters = abstractMeterRepository.findAll();
        for (var domain : List.of(Domain.heat,Domain.electricity,Domain.none) ) {

            var filtered = meters.stream().filter(it -> it.getDomain() == domain).toList();
            var data = this.calculateAbstractMeters(filtered, ts);
            for (AbstractMeterConfig meter : filtered) {
                var meterName = meter.getName().name();
                try {
                    var value = data.get(meterName);
                    MeasurementDAORequest influxRequest = MeasurementDAORequest.create(meter);
                    var fieldName = meter.getMeasurement() != null ? meter.getMeasurement().getType().getName() : "value";
                    influxRequest.getFields().put(fieldName, value);
                    influxRequest.getFields().put("time", DateConverter.toString(ts.getTsTo()));
                    HttpResponse<String> response = httpAPIs.sendRequest(influxURL + "/api/measurement", "POST", null, influxRequest, headers);
                    if (response != null && response.statusCode() < 300) {
                        AbstractMeterDataDAO meterDataDAO = AbstractMeterDataDAO.create(meter);
                        meterDataDAO.getData().put(ts.getTsTo(), Double.parseDouble(value));
                        configuredMeters.add(meterDataDAO);
                    } else if (response != null)
                        log.error(String.format("Error saving data in Influx for abstract meter %s with domain %s: %d", meter.getName().meterLabel, meter.getDomain().toString(), response.statusCode()));
                    else
                        log.error(String.format("Error retrieving data from Influx for abstract meter %s with domain %s: NULL response", meter.getName().meterLabel, meter.getDomain().toString()));

                } catch (Exception e) {
                    log.error("Error calculating abstract meter: " + meter.getName().name() + " for: " + meter.getDomain().name(), e);
                }
            }

        } return configuredMeters;
    }

    public DataWrapperDAO calculateAbstractMeters(Domain domain, Long ts) {
        var span = MeterTimespan.init(meterPeriod, ts);
        List<AbstractMeterConfig> meters = abstractMeterRepository.findAll().stream().filter(it -> it.getDomain() == domain).toList();
        return new DataWrapperDAO(this.calculateAbstractMeters(meters, span),domain.name(),span);
    }

    public HashMap<String, String> calculateAbstractMeters(List<AbstractMeterConfig> meters, MeterTimespan span) {

        if (meters.isEmpty())
            throw new NotFoundException("There aren't abstract meters configured");
        HashMap<String, String> calculated = new HashMap<>();

        for (AbstractMeterConfig meter : meters) {

            BigDecimal value = new BigDecimal(0);
            if (meter.getCondition() == null || calculator.compare(meter.getCondition(), span.getTsFrom(), span.getTsTo())) {

//                value = calculator.calculateFormula(meter.getFormula(), from, to);
                value = calculator.calcFormula(meter.getFormula(), span.getTsFrom(), span.getTsTo());
                if (meter.getMeasurement() != null) {
//                    convert to user defined scale
                    var type = meter.getMeasurement().getType();
                    var value2 = value.multiply(BigDecimal.valueOf(1 / type.getFactor()));
                    log.info("Abstract meter: " + meter.getName().name() + "-" + meter.getDomain().name() + " = " + calculator.bigDecimalToDoubleString(value2) + ";" + calculator.bigDecimalToDoubleString(value) + " * " + meter.getMeasurement().getType().getFactor());

                    value = value2;
                } else {
                    log.info("Abstract meter: " + meter.getName().name() + "-" + meter.getDomain().name() + " = " + calculator.bigDecimalToDoubleString(value) + "  - no defined measurement");

                }
            }
            calculated.put(meter.getName().name(), calculator.bigDecimalToDoubleString(value));
        }
        return calculated;
    }


}
