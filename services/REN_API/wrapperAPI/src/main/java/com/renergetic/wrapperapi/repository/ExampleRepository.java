package com.renergetic.wrapperapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.wrapperapi.model.ExampleEntity;

public interface ExampleRepository extends JpaRepository<ExampleEntity, Long>{
	
}
