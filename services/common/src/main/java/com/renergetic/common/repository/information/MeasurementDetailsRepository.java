package com.renergetic.common.repository.information;

import com.renergetic.common.model.details.MeasurementDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface MeasurementDetailsRepository extends JpaRepository<MeasurementDetails, Long> {
	MeasurementDetails save(MeasurementDetails information);

	List<MeasurementDetails> findByMeasurementId(long resourceId);

    boolean existsByKeyAndMeasurementId(String key, Long id);

    Optional<MeasurementDetails> findByKeyAndMeasurementId(String key, Long id);
}
