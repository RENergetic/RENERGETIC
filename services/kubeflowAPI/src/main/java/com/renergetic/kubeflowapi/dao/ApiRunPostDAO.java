package com.renergetic.kubeflowapi.dao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Kubeflow run obj
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiRunPostDAO {

    @JsonProperty(required = true, value = "description")
    private String description;

    @JsonProperty(required = true, value = "name")
    private String name;

    @JsonProperty(required = true, value = "pipeline_spec")
    @SerializedName("pipeline_spec")
    private PipelineSpec pipelineSpec;

    @JsonProperty(required = true, value = "resource_references")
    @SerializedName("resource_references")
    private List<ApiResourceReference> resourceReferences;

    @JsonProperty(required = true, value = "service_account")
    @SerializedName("service_account")
    private String serviceAccount;

//    public static ApiRunPostDAO create(ApiRun run) {
//        ApiRunPostDAO runDAO = new ApiRunPostDAO();
//
//        runDAO.setDescription(run.getDescription());
//        runDAO.setName(run.getName());
//        runDAO.setPipelineSpec(run.getPipeline_spec());
//        runDAO.setResourceReferences(run.getResource_references());
//        runDAO.setServiceAccount(run.getService_account());
//
//        return runDAO;
//    }
}