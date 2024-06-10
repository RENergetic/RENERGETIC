package com.renergetic.common.repository;

import com.renergetic.common.model.RuleDefinition;
import com.renergetic.common.model.RuleDefinitionMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleDefinitionRepository extends JpaRepository<RuleDefinition, Long> {
}
