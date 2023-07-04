package com.renergetic.common.repository;

import com.renergetic.common.model.DemandDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandDefinitionRepository extends JpaRepository<DemandDefinition, Long> {
}