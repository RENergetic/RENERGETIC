package com.renergetic.ingestionapi.repository;

import com.renergetic.ingestionapi.model.RequestError;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestErrorRepository extends JpaRepository<RequestError, Long> {
	
}
