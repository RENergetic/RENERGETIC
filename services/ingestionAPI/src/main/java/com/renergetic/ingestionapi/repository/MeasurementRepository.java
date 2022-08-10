package com.renergetic.ingestionapi.repository;

import com.renergetic.ingestionapi.model.Measurement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
	
	List<Measurement> findByAssetIsNull();
}
