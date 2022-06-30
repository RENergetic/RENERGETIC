package com.renergetic.backdb.repository;

import com.renergetic.backdb.model.DemandDefinition;
import com.renergetic.backdb.model.DemandSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DemandDefinitionRepository extends JpaRepository<DemandDefinition, Long> {
}