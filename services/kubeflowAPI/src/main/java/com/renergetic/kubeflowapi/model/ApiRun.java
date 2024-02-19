package com.renergetic.kubeflowapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ApiRun {

    public ApiRun() {
        
    }
    
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


    //TODO: crear método para que se mande este objeto como string/json
}
// pipeline_id: 5cda125a-2973-467b-b58b-b1c1cab712e6
// experiment_id: a8069584-de7f-4a31-9c1d-6d4a04ce66a8
/*
package com.renergetic.kubeflowapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class PipelineSpec {

    @Id
    private String pipeline_id;
    
    private String pipeline_name;

    private String workflow_manifest;

    private String pipeline_manifest;

    private Object[] parameters; //  name: String; value: String;
    
}
 */