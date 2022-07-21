package com.renergetic.backdb.repository;

import com.renergetic.backdb.model.DemandDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandDefinitionRepository extends JpaRepository<DemandDefinition, Long> {
}