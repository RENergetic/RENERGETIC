package com.renergetic.hdrapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.AlertThreshold;

@SuppressWarnings("unchecked")
public interface AlertThresholdRepository extends JpaRepository<AlertThreshold, Long> {
	
	AlertThreshold save(AlertThreshold area);
}
