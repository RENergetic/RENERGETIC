package com.renergetic.common.repository;

import com.renergetic.common.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeasurementAggregationRepository extends JpaRepository<MeasurementAggregation, Long> {
	List<MeasurementAggregation> findByOutputMeasurementsAsset (Asset asset);
}
