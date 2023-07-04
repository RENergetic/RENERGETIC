package com.renergetic.common.repository;

import com.renergetic.common.model.AlertThreshold;
import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("unchecked")
public interface AlertThresholdRepository extends JpaRepository<AlertThreshold, Long> {
	
	AlertThreshold save(AlertThreshold area);
}
