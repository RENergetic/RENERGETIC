package com.renergetic.common.repository;

import com.renergetic.common.model.HDRRequest;
import com.renergetic.common.model.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("unchecked")
public interface WorkFlowRepository extends JpaRepository<WorkflowDefinition, Long> {


    WorkflowDefinition save(WorkflowDefinition request);


}
