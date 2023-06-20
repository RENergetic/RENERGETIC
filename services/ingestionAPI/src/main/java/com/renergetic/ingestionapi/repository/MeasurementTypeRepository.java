package com.renergetic.ingestionapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.ingestionapi.model.MeasurementType;

public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {

    Optional<MeasurementType> findByName(String name);
}
