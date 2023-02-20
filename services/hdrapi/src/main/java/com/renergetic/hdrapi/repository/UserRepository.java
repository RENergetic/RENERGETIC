package com.renergetic.hdrapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@SuppressWarnings("unchecked")
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User save(User user);

	@Query("SELECT u FROM User u WHERE u.keycloakId = :keycloakId")
	User findByKeycloakId(@Param("keycloakId") String keycloakId );

	
}
