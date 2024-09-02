package com.renergetic.common.repository;

import com.renergetic.common.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
	
}
