package com.renergetic.common.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.common.model.WorkflowRun;

public interface WorkFlowRunRepositoryTemp extends JpaRepository<WorkflowRun, String> {
    WorkflowRun save(WorkflowRun workflowRun);

}
