package com.renergetic.common.repository;

import com.renergetic.common.model.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@SuppressWarnings("unchecked")
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {
	UserRoles save(UserRoles role);
	
	List<UserRoles> findByUserId(Long userId);
}
