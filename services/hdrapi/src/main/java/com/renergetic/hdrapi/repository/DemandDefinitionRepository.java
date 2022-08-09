package com.renergetic.hdrapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.DemandDefinition;

public interface DemandDefinitionRepository extends JpaRepository<DemandDefinition, Long> {
}