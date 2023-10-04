package com.renergetic.common.repository;

import com.renergetic.common.model.NotificationDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@SuppressWarnings("unchecked")
public interface NotificationDefinitionRepository extends JpaRepository<NotificationDefinition, Long> {
	
	NotificationDefinition save(NotificationDefinition definition);

	boolean existsByIdOrCode(Long id, String code);
	
	boolean existsByCode(String code);
	
	Optional<NotificationDefinition> findByCode(String code);
}
