package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.MeasurementType;

@SuppressWarnings("unchecked")
public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {
	List<MeasurementType> findByName (String name);
	
	MeasurementType save(MeasurementType asset);
}
