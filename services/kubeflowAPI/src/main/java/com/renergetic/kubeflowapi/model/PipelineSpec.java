package com.renergetic.kubeflowapi.model;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PipelineSpec {

    private String pipeline_id;

    private String pipeline_name;

    private String workflow_manifest;

    private String pipeline_manifest;

    private List<KeyValueParam> parameters; //  name: String; value: String;

}