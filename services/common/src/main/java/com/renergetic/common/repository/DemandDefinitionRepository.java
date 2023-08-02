package com.renergetic.common.repository;

import com.renergetic.common.model.DemandDefinition;
import com.renergetic.common.model.DemandDefinitionAction;
import com.renergetic.common.model.DemandDefinitionActionType;
import com.renergetic.common.model.DemandSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DemandDefinitionRepository extends JpaRepository<DemandDefinition, Long> {
    Optional<DemandDefinition> findByActionTypeAndActionAndMessage(DemandDefinitionActionType type, DemandDefinitionAction action, String message);
}