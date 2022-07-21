package com.renergetic.backdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.User;

@SuppressWarnings("unchecked")
public interface UserRepository extends JpaRepository<User, Long> {
	User save(User user);
	
}
