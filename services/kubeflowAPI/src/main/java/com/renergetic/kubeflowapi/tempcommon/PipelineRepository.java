package com.renergetic.kubeflowapi.tempcommon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface PipelineRepository extends JpaRepository<PipelineDefinition, String> {
    PipelineDefinition save(PipelineDefinition pipelineDefinition);

    @Override
    @Query(value = "SELECT wd FROM PipelineDefinition wd order by wd.pipelineId ASC")
    public List<PipelineDefinition> findAll();

    @Query(value = "SELECT wd  FROM PipelineDefinition wd "+
            "  WHERE wd.visible = :visible order by wd.pipelineId ASC")
    public List<PipelineDefinition> findByVisible(boolean visible);

    @Query(value = "SELECT wd FROM PipelineDefinition wd WHERE wd.pipelineId = :pipelineId order by wd.pipelineId ASC")
    public Optional<PipelineDefinition> findById(String pipelineId);
}
