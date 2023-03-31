package com.renergetic.hdrapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("unchecked")
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {
	UserRoles save(UserRoles role);
	
	List<UserRoles> findByUserId(Long userId);
}
