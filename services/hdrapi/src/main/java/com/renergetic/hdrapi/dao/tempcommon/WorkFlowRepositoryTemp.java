package com.renergetic.hdrapi.dao.tempcommon;


import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkFlowRepositoryTemp extends JpaRepository<WorkflowDefinition, Long> {


    WorkflowDefinition save(WorkflowDefinition request);


}
