package com.renergetic.backdb.repository;

import com.renergetic.backdb.model.details.MeasurementTags;

import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("unchecked")
public interface MeasurementTagsRepository extends JpaRepository<MeasurementTags, Long> {
	
	MeasurementTags save(MeasurementTags tag);
}
