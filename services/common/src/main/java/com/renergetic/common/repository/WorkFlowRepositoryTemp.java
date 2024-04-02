package com.renergetic.common.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.renergetic.common.model.WorkflowDefinition;

import java.util.List;
import java.util.Optional;

public interface WorkFlowRepositoryTemp extends JpaRepository<WorkflowDefinition, String> {


    WorkflowDefinition save(WorkflowDefinition workflowDefinition);

    @Override
    @Query(value = "SELECT wd FROM WorkflowDefinition wd order by wd.experimentId ASC" )
    public List<WorkflowDefinition> findAll();
    @Query(value = "SELECT wd FROM WorkflowDefinition wd WHERE wd.visible = :visible order by wd.experimentId ASC" )
    public List<WorkflowDefinition> findByVisible(boolean visible);
    @Query(value = "SELECT wd FROM WorkflowDefinition wd WHERE wd.experimentId = :experimentId order by wd.experimentId ASC" )
    public Optional<WorkflowDefinition> findById(String experimentId);


}
