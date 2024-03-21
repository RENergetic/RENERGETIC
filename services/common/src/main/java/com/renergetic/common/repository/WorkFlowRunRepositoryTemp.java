package com.renergetic.common.repository;


import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkFlowRunRepositoryTemp extends JpaRepository<WorkflowRun, Long> {
    WorkflowRun save(WorkflowRun workflowRun);

}
