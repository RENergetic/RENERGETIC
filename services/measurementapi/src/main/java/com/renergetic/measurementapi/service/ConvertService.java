package com.renergetic.measurementapi.service;

import com.renergetic.measurementapi.dao.MeasurementDAOResponse;
import com.renergetic.measurementapi.model.MeasurementType;
import com.renergetic.measurementapi.repository.DashboardRepository;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConvertService {

    @Autowired
    DashboardService dashboardService;

    @Autowired
    MeasurementTypeService measurementTypeService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DecimalFormat df = new DecimalFormat("0");

    public ConvertService(){
        df.setMaximumFractionDigits(10);
    }

    public List<MeasurementDAOResponse> convert(List<MeasurementDAOResponse> measurements, String dashboardId,
                                                List<String> fields, String function){
        MeasurementType dashboardUnit = dashboardService.getDashboardUnitByGrafanaId(dashboardId);

        if(dashboardUnit == null)
            return measurements;

        Map<String, TriFunction<String, LocalDateTime, LocalDateTime, String>> converters =
                retrieveAllConversionFunctions(dashboardUnit, fields, function);

        //Ensure that we don't iterate over the whole list if we have no applicable conversion.
        if(isEmptyConverters(converters))
            return measurements;

        return convertMeasurements(measurements, converters);
    }

    protected List<MeasurementDAOResponse> convertMeasurements(List<MeasurementDAOResponse> measurements,
                                                               Map<String, TriFunction<String, LocalDateTime, LocalDateTime, String>> converters){
        LocalDateTime previousTimestamp = null;
        for(MeasurementDAOResponse m : measurements){
            Map<String, String> mFields = m.getFields();
            Map<String, String> convertedFields = new HashMap<>();

            String currentTimestampRaw = mFields.get("time");
            final LocalDateTime cTs = LocalDateTime.parse(currentTimestampRaw, formatter);
            convertedFields.put("time", currentTimestampRaw);

            final LocalDateTime pTs = previousTimestamp;
            mFields.forEach((name, value) -> {
                if(name.equals("time"))
                    return;
                TriFunction<String, LocalDateTime, LocalDateTime, String> converter = converters.getOrDefault(name, null);
                convertedFields.put(name, converter == null ? value : converter.apply(value, pTs, cTs));
            });

            m.setFields(convertedFields);
            previousTimestamp = cTs;
        }
        return measurements;
    }

    protected Map<String, TriFunction<String, LocalDateTime, LocalDateTime, String>> retrieveAllConversionFunctions
            (MeasurementType dashboardUnit, List<String> fields, String function){
        Map<String, TriFunction<String, LocalDateTime, LocalDateTime, String>> converters = new HashMap<>();
        for(String field : fields)
            converters.put(function == null ? field : function, retrieveConversionFunction(dashboardUnit, measurementTypeService.getTypeByName(field)));
        return converters;
    }

    protected TriFunction<String, LocalDateTime, LocalDateTime, String> retrieveConversionFunction(MeasurementType dashboardUnit, MeasurementType measurementType){
        if(dashboardUnit == null || measurementType == null)
            return null;

        final double factor = measurementType.getFactor() / dashboardUnit.getFactor();
        switch (measurementType.getBaseUnit()){
            case "Wh":
                switch (dashboardUnit.getBaseUnit()){
                    case "Wh":
                        return factor == 1.0 ? null : (fieldValue, pTs, cTs) ->
                                df.format(Double.parseDouble(fieldValue) * factor);
                    case "W":
                        return (fieldValue, pTs, cTs) ->
                                df.format(Double.parseDouble(fieldValue) * factor / getRatio(pTs, cTs));
                    default:
                        return null;
                }
            case "W":
                switch (dashboardUnit.getBaseUnit()){
                    case "W":
                        return factor == 1.0 ? null : (fieldValue, pTs, cTs) ->
                                df.format(Double.parseDouble(fieldValue) * factor);
                    case "Wh":
                        return (fieldValue, pTs, cTs) ->
                                df.format(Double.parseDouble(fieldValue) * factor * getRatio(pTs, cTs));
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    protected Double getRatio(LocalDateTime pTs, LocalDateTime cTs){
        if(pTs == null)
            return 0.25;

        long diffInMinutes = ChronoUnit.MINUTES.between(pTs, cTs);
        return diffInMinutes / 60.0;
    }

    protected boolean isEmptyConverters(Map<String, TriFunction<String, LocalDateTime, LocalDateTime, String>> converters){
        for(TriFunction<String, LocalDateTime, LocalDateTime, String> convertFunction : converters.values())
            if(convertFunction != null)
                return false;
        return true;
    }
}
