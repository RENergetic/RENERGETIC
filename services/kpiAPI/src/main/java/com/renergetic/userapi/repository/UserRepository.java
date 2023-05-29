package com.renergetic.userapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.userapi.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByKeycloakId(String id);

	boolean existsByKeycloakId(String id);

}
