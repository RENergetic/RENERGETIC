package com.renergetic.kubeflowapi.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PipelineSpec {

    @Id
    @Column(name = "pipeline_id")
    @JsonProperty(required = true, value = "pipeline_id")
    private String pipelineId;

    @Column(nullable = true, name = "pipeline_name")
    @JsonProperty(required = true, value = "pipeline_name")
    private String pipelineName;

    @Column(nullable = true, name = "workflow_manifest")
    @JsonProperty(required = true, value = "workflow_manifest")
    private String workflowManifest;

    @Column(nullable = true, name = "pipeline_manifest")
    @JsonProperty(required = true, value = "pipeline_manifest")
    private String pipelineManifest;

    @OneToMany()
    @JoinColumn(name = "parameter", nullable = false, updatable = true)
    private List<KeyValue> parameters; 

}