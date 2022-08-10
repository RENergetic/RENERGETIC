package com.renergetic.ingestionapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.ingestionapi.model.MeasurementType;

public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {
}
