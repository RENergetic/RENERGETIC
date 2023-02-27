package com.renergetic.measurementapi.service;

import com.renergetic.measurementapi.model.MeasurementType;
import com.renergetic.measurementapi.repository.MeasurementTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementTypeService {
    @Autowired
    MeasurementTypeRepository measurementTypeRepository;

    public MeasurementType getTypeByName(String measurementTypeName) {
        return measurementTypeRepository.findByName(measurementTypeName);
    }
}
