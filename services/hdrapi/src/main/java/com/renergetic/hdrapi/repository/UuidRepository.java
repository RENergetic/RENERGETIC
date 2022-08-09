package com.renergetic.backdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.UUID;
import com.renergetic.backdb.model.User;

public interface UuidRepository extends JpaRepository<UUID, String>{
	User save(User user);
}
