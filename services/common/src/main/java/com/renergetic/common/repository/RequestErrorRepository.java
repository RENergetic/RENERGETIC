package com.renergetic.common.repository;

import com.renergetic.common.model.RequestError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestErrorRepository extends JpaRepository<RequestError, Long> {
	
}
