package com.renergetic.hdrapi.dao.temp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class WorkflowDefinitionDAO {

    @JsonProperty(required = true,value = "experiment_id")
    String experimentId;
    @JsonProperty(required = false,value = "name")
    String name;
    @JsonProperty(required = false)
    List<WorkflowPipelineDAO> pipelines;
    @JsonProperty(required = false,value = "parameters")
    Map<String,Object> parameters;
    @JsonProperty(required = true,value = "visible")
    boolean visible =false;
    @JsonProperty(required = false,value = "current_run")
    WorkflowRunDAO currentRun;

    public WorkflowDefinitionDAO(String experimentId){
        this.experimentId=experimentId;
    }

}
