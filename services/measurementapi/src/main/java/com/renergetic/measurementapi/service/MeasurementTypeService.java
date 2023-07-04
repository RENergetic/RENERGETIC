package com.renergetic.measurementapi.service;

import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.repository.MeasurementTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeasurementTypeService {
    @Autowired
    MeasurementTypeRepository measurementTypeRepository;

    public MeasurementType getTypeByName(String measurementTypeName) {
        return measurementTypeRepository.findByName(measurementTypeName);
    }

    public MeasurementType getTypeByUnit(String unit) {
        return measurementTypeRepository.findByUnit(unit);
    }
}
