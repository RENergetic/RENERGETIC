package com.renergetic.measurementapi.repository;

import com.renergetic.measurementapi.model.MeasurementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@SuppressWarnings("unchecked")
public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {
	MeasurementType findByName (String name);
}
