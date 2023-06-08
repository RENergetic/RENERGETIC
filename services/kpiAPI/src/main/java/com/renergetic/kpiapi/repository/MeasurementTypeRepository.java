package com.renergetic.kpiapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.kpiapi.model.MeasurementType;

public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {
    
}
