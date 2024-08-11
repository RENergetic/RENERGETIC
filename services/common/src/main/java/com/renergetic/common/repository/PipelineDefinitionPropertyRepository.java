package com.renergetic.common.repository;


import com.renergetic.common.model.PipelineDefinition;
import com.renergetic.common.model.PipelineDefinitionProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.JoinColumn;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface PipelineDefinitionPropertyRepository extends JpaRepository<PipelineDefinitionProperty, String> {


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM pipeline_definition_property WHERE " +
            " pipeline_definition_property.pipeline_id = :pipelineId AND pipeline_definition_property.key=:key ",
            nativeQuery = true)
    void deletePipelineProperty(String pipelineId, String key);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM pipeline_definition_property WHERE  pipeline_definition_property.key=:key ",
            nativeQuery = true)
    void deletePipelineProperty(  String key);

    @Query(value = "SELECT property FROM PipelineDefinitionProperty property WHERE " +
            " property.pipelineDefinition.pipelineId = :pipelineId AND property.key=:key ")
    public Optional<PipelineDefinitionProperty> getPipelineProperty(String pipelineId, String key);
}

