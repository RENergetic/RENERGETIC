package com.renergetic.kubeflowapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.kubeflowapi.model.ApiExperiment;

public interface ApiRunRepository extends JpaRepository<ApiExperiment, String> {

}
