package com.renergetic.hdrapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.renergetic.hdrapi.model.details.MeasurementTags;

@SuppressWarnings("unchecked")
public interface MeasurementTagsRepository extends JpaRepository<MeasurementTags, Long> {
	
	MeasurementTags save(MeasurementTags tag);
	
	@Query(value = "SELECT t.* " +
			"FROM (tags t " +
			"INNER JOIN measurement_tags connection ON connection.tag_id = t.id) " +
			"WHERE connection.measurement_id = :measurementId", nativeQuery = true)
	List<MeasurementTags> findByMeasurementId(Long measurementId);
}
