package com.renergetic.common.dao;

import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.Rule;
import com.renergetic.common.model.RuleDefinitionMeasurement;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.common.utilities.TimeFormatValidator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class RuleDefinitionMeasurementDAO {
    @Autowired
    private MeasurementRepository measurementRepository;

    private Long id;
    private String function;
    private Float multiplier;
    private Long measurementId;
    private String rangeFrom;
    private String rangeTo;

    public RuleDefinitionMeasurement mapToEntity(boolean includeFullMeasurement){
        if(includeFullMeasurement)
            return mapToEntity(measurementRepository.findById(getMeasurementId()).orElseThrow(NotFoundException::new));

        Measurement measurement = new Measurement();
        measurement.setId(getMeasurementId());
        return mapToEntity(measurement);
    }

    public RuleDefinitionMeasurement mapToEntity(Measurement measurement){
        TimeFormatValidator.validateTimeDifferenceFromNow(getRangeFrom());
        TimeFormatValidator.validateTimeDifferenceFromNow(getRangeTo());

        RuleDefinitionMeasurement entity = new RuleDefinitionMeasurement();
        entity.setId(getId());
        entity.setFunction(getFunction());
        entity.setMultiplier(getMultiplier());
        entity.setMeasurement(measurement);
        entity.setRangeFrom(getRangeFrom());
        entity.setRangeTo(getRangeTo());
        return entity;
    }

    public static RuleDefinitionMeasurementDAO fromEntity(RuleDefinitionMeasurement entity){
        RuleDefinitionMeasurementDAO dao = new RuleDefinitionMeasurementDAO();
        dao.setId(entity.getId());
        dao.setFunction(entity.getFunction());
        dao.setMultiplier(entity.getMultiplier());
        dao.setMeasurementId(entity.getMeasurement().getId());
        dao.setRangeFrom(entity.getRangeFrom());
        dao.setRangeTo(entity.getRangeTo());
        return dao;
    }
}
