package com.renergetic.kubeflowapi.dao.tempcommon;


import com.renergetic.common.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkFlowRepositoryTemp extends JpaRepository<WorkflowDefinition, Long> {


    WorkflowDefinition save(WorkflowDefinition workflowDefinition);

    @Query(value = "SELECT wd FROM WorkflowDefinition wd WHERE wd.visible = :visible" )
    public List<WorkflowDefinition> findByVisible(boolean visible);
    @Query(value = "SELECT wd FROM WorkflowDefinition wd WHERE wd.experimentId = :experimentId" )
    public Optional<WorkflowDefinition> findById(String experimentId);


}
