package com.renergetic.kubeflowapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.kubeflowapi.model.ExampleEntity;

public interface ExampleRepository extends JpaRepository<ExampleEntity, Long>{
	
}
