package com.renergetic.hdrapi.repository.information;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.details.MeasurementDetails;

@SuppressWarnings("unchecked")
public interface MeasurementDetailsRepository extends JpaRepository<MeasurementDetails, Long> {
	MeasurementDetails save(MeasurementDetails information);

	List<MeasurementDetails> findByMeasurementId(long resource_id);

    boolean existsByKeyAndMeasurementId(String key, Long id);

    Optional<MeasurementDetails> findByKeyAndMeasurementId(String key, Long id);
}
