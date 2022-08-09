package com.renergetic.hdrapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.User;

@SuppressWarnings("unchecked")
public interface UserRepository extends JpaRepository<User, Long> {
	User save(User user);
	
}
