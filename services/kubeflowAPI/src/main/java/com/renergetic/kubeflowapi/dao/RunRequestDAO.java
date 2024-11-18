package com.renergetic.kubeflowapi.dao;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Kubeflow run obj
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RunRequestDAO {




    @JsonProperty(value = "pipeline_id", required = true)
    private String pipelineId;
    @JsonProperty(value = "simulation_name", required = false)
    private String simulationName;

    @JsonProperty(required = false)
    private Map<String,Object> params = new HashMap<>();
    @JsonProperty(required = false)
    private Map<String, Object> ext= new HashMap<>();;




}
