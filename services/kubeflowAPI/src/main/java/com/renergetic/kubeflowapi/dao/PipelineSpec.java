package com.renergetic.kubeflowapi.dao;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
/**
 * kubeflow pipeline obj
 */
public class PipelineSpec {

    @JsonProperty(required = true, value = "pipeline_id")
    @SerializedName( "pipeline_id")
    private String pipelineId;

    @JsonProperty(required = true, value = "pipeline_name")
    @SerializedName( "pipeline_name")
    private String pipelineName;

    @JsonProperty(required = true, value = "workflow_manifest")
    @SerializedName( "workflow_manifest")
    private String workflowManifest;

    @JsonProperty(required = true, value = "pipeline_manifest")
    @SerializedName( "pipeline_manifest")
    private String pipelineManifest;

    private List<KeyValue> parameters;

}