package com.renergetic.kubeflowapi.dao.tempcommon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WorkflowRunDAO {

    @JsonProperty(required = true,value = "run_id")
    String runId;
    @JsonProperty(required = true,value = "workflow")
    WorkflowDefinitionDAO workflowDefinition;
    @JsonProperty(required = true,value = "parameters")
    Map<String,Object> parameters = Collections.emptyMap();
    @JsonProperty(required = false,value = "start_time" )
    private long startTime;
    @JsonProperty(required = false,value = "end_time" )
    private long endTime;

    public  WorkflowRunDAO(String runId,WorkflowDefinitionDAO workflowDefinition){
        this.runId=runId;
        this.workflowDefinition=workflowDefinition;
    }

}
