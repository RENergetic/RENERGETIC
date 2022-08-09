package com.renergetic.hdrapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.UUID;
import com.renergetic.hdrapi.model.User;

public interface UuidRepository extends JpaRepository<UUID, String>{
	User save(User user);
}
