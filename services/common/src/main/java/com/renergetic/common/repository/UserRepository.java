package com.renergetic.common.repository;

import com.renergetic.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unchecked")
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User save(User user);

	@Query("SELECT u FROM User u WHERE u.keycloakId = :keycloakId")
	User findByKeycloakId(@Param("keycloakId") String keycloakId );

	
}
