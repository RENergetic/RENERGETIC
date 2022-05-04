package com.renergetic.backdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.AlertThreshold;

@SuppressWarnings("unchecked")
public interface AlertThresholdRepository extends JpaRepository<AlertThreshold, Long> {
	
	AlertThreshold save(AlertThreshold area);
}
