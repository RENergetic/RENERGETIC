package com.renergetic.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.api.model.ExampleEntity;

public interface ExampleRepository extends JpaRepository<ExampleEntity, Long>{
	
}
