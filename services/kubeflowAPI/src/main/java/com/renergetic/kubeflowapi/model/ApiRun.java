package com.renergetic.kubeflowapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ApiRun {
    
    private String id;

    private String name;

    private String storage_state; // x ∈ { STORAGESTATE_AVAILABLE (default) , STORAGESTATE_ARCHIVED }

    private String description;

    private PipelineSpec pipeline_spec;

    private Object[] resource_references;

    private String service_account;

    private String createt_at;

    private String scheduled_at;

    private String finished_at;

    private String status;

    private String error;

    private Object[] metrics;

}