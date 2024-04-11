package com.renergetic.kubeflowapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.kubeflowapi.model.ApiExperiment;

public interface ExperimentRepository extends JpaRepository<ApiExperiment, String>{
	
}
