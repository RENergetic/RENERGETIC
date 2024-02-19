package com.renergetic.kubeflowapi.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiRunPost {


    private String description;

    private String name;

    private PipelineSpec pipeline_spec;

    private List<ApiResourceReference> resource_references;

    private String service_account;



    //TODO: crear método para que se mande este objeto como string/json
}
// pipeline_id: 5cda125a-2973-467b-b58b-b1c1cab712e6
// experiment_id: a8069584-de7f-4a31-9c1d-6d4a04ce66a8
