package com.renergetic.hdrapi.repository.information;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.details.MeasurementDetails;

@SuppressWarnings("unchecked")
public interface MeasurementDetailsRepository extends JpaRepository<MeasurementDetails, Long> {
	MeasurementDetails save(MeasurementDetails information);

	List<MeasurementDetails> findByMeasurementId(long resource_id);
}
