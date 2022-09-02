package com.renergetic.ingestionapi.repository;

import com.renergetic.ingestionapi.model.Request;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
	
}
