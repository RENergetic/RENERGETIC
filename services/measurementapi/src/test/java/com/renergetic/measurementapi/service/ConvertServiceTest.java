package com.renergetic.measurementapi.service;

import com.renergetic.measurementapi.dao.MeasurementDAOResponse;
import com.renergetic.measurementapi.model.DashboardUnit;
import com.renergetic.measurementapi.model.MeasurementType;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConvertServiceTest {
    private ConvertService convertService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<MeasurementDAOResponse> measurementDAOResponseList;

    @Before
    public void init() {
        convertService = new ConvertService();

        measurementDAOResponseList = new ArrayList<>();
    }

    public void generateNonFunctionMeasurements(){
        MeasurementDAOResponse measurementDAOResponse = new MeasurementDAOResponse();
        Map<String, String> map = new HashMap<>();

        map.put("energy", "1.0");
        map.put("power_kw", "1.0");
        map.put("time", "2023-02-24 08:00:00");
        measurementDAOResponse.setFields(map);
        measurementDAOResponseList.add(measurementDAOResponse);

        measurementDAOResponse = new MeasurementDAOResponse();
        map = new HashMap<>();
        map.put("energy", "2.0");
        map.put("power_kw", "2.0");
        map.put("time", "2023-02-24 08:15:00");
        measurementDAOResponse.setFields(map);
        measurementDAOResponseList.add(measurementDAOResponse);

        measurementDAOResponse = new MeasurementDAOResponse();
        map = new HashMap<>();
        map.put("energy", "1.0");
        map.put("power_kw", "1.0");
        map.put("time", "2023-02-24 08:45:00");
        measurementDAOResponse.setFields(map);
        measurementDAOResponseList.add(measurementDAOResponse);

        measurementDAOResponse = new MeasurementDAOResponse();
        map = new HashMap<>();
        map.put("energy", "0.5");
        map.put("power_kw", "0.5");
        map.put("time", "2023-02-24 09:00:00");
        measurementDAOResponse.setFields(map);
        measurementDAOResponseList.add(measurementDAOResponse);
    }

    public void generateFunctionMeasurements(){
        MeasurementDAOResponse measurementDAOResponse = new MeasurementDAOResponse();
        Map<String, String> map = new HashMap<>();

        map.put("sum", "1.0");
        map.put("time", "2023-02-24 08:00:00");
        measurementDAOResponse.setFields(map);
        measurementDAOResponseList.add(measurementDAOResponse);

        measurementDAOResponse = new MeasurementDAOResponse();
        map = new HashMap<>();
        map.put("sum", "2.0");
        map.put("time", "2023-02-24 08:15:00");
        measurementDAOResponse.setFields(map);
        measurementDAOResponseList.add(measurementDAOResponse);

        measurementDAOResponse = new MeasurementDAOResponse();
        map = new HashMap<>();
        map.put("sum", "1.0");
        map.put("time", "2023-02-24 08:45:00");
        measurementDAOResponse.setFields(map);
        measurementDAOResponseList.add(measurementDAOResponse);

        measurementDAOResponse = new MeasurementDAOResponse();
        map = new HashMap<>();
        map.put("sum", "0.5");
        map.put("time", "2023-02-24 09:00:00");
        measurementDAOResponse.setFields(map);
        measurementDAOResponseList.add(measurementDAOResponse);
    }

    @Test
    public void test_isEmptyConverters_emptyMap(){
        Assert.assertTrue(convertService.isEmptyConverters(new HashMap<>()));
    }

    @Test
    public void test_isEmptyConverters_filledMap(){
        Map<String, TriFunction<String, LocalDateTime, LocalDateTime, String>> converters = new HashMap<>();
        converters.put("test", (a, b, c) -> null);
        Assert.assertFalse(convertService.isEmptyConverters(converters));
    }

    @Test
    public void test_getRatio_15Mins(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 08:15:00", formatter);
        Assert.assertEquals((Double)0.25, convertService.getRatio(pTs, cTs));
    }

    @Test
    public void test_getRatio_60Mins(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 09:00:00", formatter);
        Assert.assertEquals((Double)1.0, convertService.getRatio(pTs, cTs));
    }

    @Test
    public void test_getRatio_120Mins(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 10:00:00", formatter);
        Assert.assertEquals((Double)2.0, convertService.getRatio(pTs, cTs));
    }

    @Test
    public void test_retrieveConversionFunction_MWhToJ(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 08:15:00", formatter);
        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("Wh");
        dashboardUnit.setFactor(3600.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("Wh");
        measurementType.setFactor(1000000.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converter =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        Assert.assertEquals("3600000000", converter.apply("1.0", pTs, cTs));
    }

    @Test
    public void test_retrieveConversionFunction_WhToJ(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 08:15:00", formatter);
        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("Wh");
        dashboardUnit.setFactor(3600.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("Wh");
        measurementType.setFactor(1.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converter =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        Assert.assertEquals("3600", converter.apply("1.0", pTs, cTs));
    }

    @Test
    public void test_retrieveConversionFunction_MWhToW(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 08:15:00", formatter);
        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("W");
        dashboardUnit.setFactor(1.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("Wh");
        measurementType.setFactor(1000000.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converter =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        Assert.assertEquals("4000000", converter.apply("1.0", pTs, cTs));
    }

    @Test
    public void test_retrieveConversionFunction_WhToW(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 08:15:00", formatter);
        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("W");
        dashboardUnit.setFactor(1.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("Wh");
        measurementType.setFactor(1.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converter =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        Assert.assertEquals("4", converter.apply("1.0", pTs, cTs));
    }

    @Test
    public void test_retrieveConversionFunction_WhToWh(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 08:15:00", formatter);
        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("Wh");
        dashboardUnit.setFactor(1.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("Wh");
        measurementType.setFactor(1.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converter =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        Assert.assertNull(converter);
    }

    @Test
    public void test_retrieveConversionFunction_WToW(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 08:15:00", formatter);
        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("W");
        dashboardUnit.setFactor(1.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("W");
        measurementType.setFactor(1.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converter =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        Assert.assertNull(converter);
    }

    @Test
    public void test_retrieveConversionFunction_MWToJ(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 08:15:00", formatter);
        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("Wh");
        dashboardUnit.setFactor(3600.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("W");
        measurementType.setFactor(1000000.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converter =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        Assert.assertEquals("900000000", converter.apply("1.0", pTs, cTs));
    }

    @Test
    public void test_retrieveConversionFunction_WToJ(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 08:15:00", formatter);
        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("Wh");
        dashboardUnit.setFactor(3600.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("W");
        measurementType.setFactor(1.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converter =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        Assert.assertEquals("900", converter.apply("1.0", pTs, cTs));
    }

    @Test
    public void test_retrieveConversionFunction_MWToWh(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 08:15:00", formatter);
        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("Wh");
        dashboardUnit.setFactor(1.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("W");
        measurementType.setFactor(1000000.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converter =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        Assert.assertEquals("250000", converter.apply("1.0", pTs, cTs));
    }

    @Test
    public void test_retrieveConversionFunction_WToWh(){
        LocalDateTime pTs = LocalDateTime.parse("2023-02-24 08:00:00", formatter);
        LocalDateTime cTs = LocalDateTime.parse("2023-02-24 08:15:00", formatter);
        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("Wh");
        dashboardUnit.setFactor(1.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("W");
        measurementType.setFactor(1.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converter =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        Assert.assertEquals("0.25", converter.apply("1.0", pTs, cTs));
    }

    @Test
    public void test_convertMeasurements_noFunction(){
        generateNonFunctionMeasurements();

        Map<String, TriFunction<String, LocalDateTime, LocalDateTime, String>> converters = new HashMap<>();

        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("Wh");
        dashboardUnit.setFactor(1.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("W");
        measurementType.setFactor(1000.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converterA =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        measurementType = new MeasurementType();
        measurementType.setBaseUnit("Wh");
        measurementType.setFactor(1000000.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converterB =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        converters.put("power_kw", converterA);
        converters.put("energy", converterB);

        List<MeasurementDAOResponse> list = convertService.convertMeasurements(measurementDAOResponseList, converters);

        Map<String, String> fields = list.get(0).getFields();
        Assert.assertEquals("2023-02-24 08:00:00", fields.get("time"));
        Assert.assertEquals("250", fields.get("power_kw"));
        Assert.assertEquals("1000000", fields.get("energy"));

        fields = list.get(1).getFields();
        Assert.assertEquals("2023-02-24 08:15:00", fields.get("time"));
        Assert.assertEquals("500", fields.get("power_kw"));
        Assert.assertEquals("2000000", fields.get("energy"));

        fields = list.get(2).getFields();
        Assert.assertEquals("2023-02-24 08:45:00", fields.get("time"));
        Assert.assertEquals("500", fields.get("power_kw"));
        Assert.assertEquals("1000000", fields.get("energy"));

        fields = list.get(3).getFields();
        Assert.assertEquals("2023-02-24 09:00:00", fields.get("time"));
        Assert.assertEquals("125", fields.get("power_kw"));
        Assert.assertEquals("500000", fields.get("energy"));
    }

    @Test
    public void test_convertMeasurements_noFunction2(){
        generateNonFunctionMeasurements();

        Map<String, TriFunction<String, LocalDateTime, LocalDateTime, String>> converters = new HashMap<>();

        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("W");
        dashboardUnit.setFactor(1.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("W");
        measurementType.setFactor(1000.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converterA =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        measurementType = new MeasurementType();
        measurementType.setBaseUnit("Wh");
        measurementType.setFactor(1000000.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converterB =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        converters.put("power_kw", converterA);
        converters.put("energy", converterB);

        List<MeasurementDAOResponse> list = convertService.convertMeasurements(measurementDAOResponseList, converters);

        Map<String, String> fields = list.get(0).getFields();
        Assert.assertEquals("2023-02-24 08:00:00", fields.get("time"));
        Assert.assertEquals("1000", fields.get("power_kw"));
        Assert.assertEquals("4000000", fields.get("energy"));

        fields = list.get(1).getFields();
        Assert.assertEquals("2023-02-24 08:15:00", fields.get("time"));
        Assert.assertEquals("2000", fields.get("power_kw"));
        Assert.assertEquals("8000000", fields.get("energy"));

        fields = list.get(2).getFields();
        Assert.assertEquals("2023-02-24 08:45:00", fields.get("time"));
        Assert.assertEquals("1000", fields.get("power_kw"));
        Assert.assertEquals("2000000", fields.get("energy"));

        fields = list.get(3).getFields();
        Assert.assertEquals("2023-02-24 09:00:00", fields.get("time"));
        Assert.assertEquals("500", fields.get("power_kw"));
        Assert.assertEquals("2000000", fields.get("energy"));
    }

    @Test
    public void test_convertMeasurements_function(){
        generateFunctionMeasurements();

        Map<String, TriFunction<String, LocalDateTime, LocalDateTime, String>> converters = new HashMap<>();

        MeasurementType dashboardUnit = new MeasurementType();
        dashboardUnit.setBaseUnit("W");
        dashboardUnit.setFactor(1.0);
        MeasurementType measurementType = new MeasurementType();
        measurementType.setBaseUnit("W");
        measurementType.setFactor(1000.0);
        TriFunction<String, LocalDateTime, LocalDateTime, String> converterA =
                convertService.retrieveConversionFunction(dashboardUnit, measurementType);

        converters.put("sum", converterA);

        List<MeasurementDAOResponse> list = convertService.convertMeasurements(measurementDAOResponseList, converters);

        Map<String, String> fields = list.get(0).getFields();
        Assert.assertEquals("2023-02-24 08:00:00", fields.get("time"));
        Assert.assertEquals("1000", fields.get("sum"));

        fields = list.get(1).getFields();
        Assert.assertEquals("2023-02-24 08:15:00", fields.get("time"));
        Assert.assertEquals("2000", fields.get("sum"));

        fields = list.get(2).getFields();
        Assert.assertEquals("2023-02-24 08:45:00", fields.get("time"));
        Assert.assertEquals("1000", fields.get("sum"));

        fields = list.get(3).getFields();
        Assert.assertEquals("2023-02-24 09:00:00", fields.get("time"));
        Assert.assertEquals("500", fields.get("sum"));
    }
}
