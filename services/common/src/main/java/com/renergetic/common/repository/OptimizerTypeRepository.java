package com.renergetic.common.repository;

import com.renergetic.common.model.Asset;
import com.renergetic.common.model.MeasurementAggregation;
import com.renergetic.common.model.OptimizerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptimizerTypeRepository extends JpaRepository<OptimizerType, Long> {
	OptimizerType findByName (String name);
}
