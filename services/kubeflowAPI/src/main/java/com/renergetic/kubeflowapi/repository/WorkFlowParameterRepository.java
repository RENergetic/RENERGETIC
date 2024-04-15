package com.renergetic.kubeflowapi.repository;


import com.renergetic.common.model.WorkflowParameter;
import com.renergetic.common.model.WorkflowRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkFlowParameterRepository extends JpaRepository<WorkflowParameter, String> {


}
