package com.renergetic.hdrapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.details.MeasurementTags;

@SuppressWarnings("unchecked")
public interface MeasurementTagsRepository extends JpaRepository<MeasurementTags, Long> {
	
	MeasurementTags save(MeasurementTags tag);
}
