package com.renergetic.common.repository;


import com.renergetic.common.model.WorkflowRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkFlowRunRepository extends JpaRepository<WorkflowRun, String> {
    WorkflowRun save(WorkflowRun workflowRun);

}
