package com.renergetic.hdrapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.NotificationDefinition;

@SuppressWarnings("unchecked")
public interface NotificationDefinitionRepository extends JpaRepository<NotificationDefinition, Long> {
	
	NotificationDefinition save(NotificationDefinition definition);

	boolean existsByIdOrCode(Long id, String code);
	
	boolean existsByCode(String code);
	
	Optional<NotificationDefinition> findByCode(String code);
}
