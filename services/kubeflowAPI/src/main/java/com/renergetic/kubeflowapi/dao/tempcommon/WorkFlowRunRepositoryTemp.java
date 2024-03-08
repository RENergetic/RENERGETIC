package com.renergetic.kubeflowapi.dao.tempcommon;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkFlowRunRepositoryTemp extends JpaRepository<WorkflowRun, Long> {
    WorkflowRun save(WorkflowRun workflowRun);

}
