package com.renergetic.common.repository;

import com.renergetic.common.model.UUID;
import com.renergetic.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UuidRepository extends JpaRepository<UUID, String>{
	User save(User user);
}
