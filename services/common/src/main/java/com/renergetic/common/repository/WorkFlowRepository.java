package com.renergetic.common.repository;

import com.renergetic.common.model.HDRRequest;
import com.renergetic.common.model.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface WorkFlowRepository extends JpaRepository<WorkflowDefinition, Long> {



    WorkflowDefinition save(WorkflowDefinition workflowDefinition);

    @Query(value = "SELECT wd FROM WorkflowDefinition wd WHERE wd.visible = :visible" )
    public List<WorkflowDefinition> findByVisible(boolean visible);
    @Query(value = "SELECT wd FROM WorkflowDefinition wd WHERE wd.experimentId = :experimentId" )
    public Optional<WorkflowDefinition> findById(String experimentId);


}
