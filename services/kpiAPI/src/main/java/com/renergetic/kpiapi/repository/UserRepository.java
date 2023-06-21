package com.renergetic.kpiapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.kpiapi.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByKeycloakId(String id);

	boolean existsByKeycloakId(String id);

}
