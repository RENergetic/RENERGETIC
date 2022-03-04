package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.UserRoles;

@SuppressWarnings("unchecked")
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {
	UserRoles save(UserRoles role);
	
	List<UserRoles> findByUserId(Long userId);
}
