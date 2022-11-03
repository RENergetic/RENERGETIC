package com.renergetic.ingestionapi.repository;

import com.renergetic.ingestionapi.model.Tags;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagsRepository extends JpaRepository<Tags, Long> {
	
	List<Tags> findByMeasurementIdIsNull();
}
