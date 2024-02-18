package com.renergetic.kubeflowapi.dao.tempcommon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.common.utilities.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.json.ParseException;

import java.util.Collections;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WorkflowRunDAO {

    @JsonProperty(required = true, value = "run_id")
    String runId;
    @JsonProperty(required = true, value = "workflow")
    WorkflowDefinitionDAO workflowDefinition;
    @JsonProperty(required = true, value = "parameters")
    Map<String, Object> parameters = Collections.emptyMap();
    @JsonProperty(required = false, value = "start_time")
    private Long startTime;
    @JsonProperty(required = false, value = "end_time")
    private Long endTime;

    public WorkflowRunDAO(String runId, WorkflowDefinitionDAO workflowDefinition) {
        this.runId = runId;
        this.workflowDefinition = workflowDefinition;
    }

    public static WorkflowRunDAO create(WorkflowRun wd) {
        WorkflowRunDAO dao = new WorkflowRunDAO();
        dao.setRunId(wd.getRunId());
        if (wd.getWorkflowDefinition() != null) {
            WorkflowDefinitionDAO workflowDefinitionDAO = new WorkflowDefinitionDAO();
            workflowDefinitionDAO.setExperimentId(wd.getWorkflowDefinition().getExperimentId());
            dao.setWorkflowDefinition(workflowDefinitionDAO);
        }
        if (wd.getParams() != null && !wd.getParams().isEmpty()) {
            try {
                Map<String, Object> map = Json.toMap(wd.getParams());
                dao.setParameters(map);
            } catch (ParseException e) {
                //TODO: throw some usable exception
                throw new RuntimeException(e);
            }
        }
        if (wd.getStartTime() != null)
            dao.setStartTime(DateConverter.toEpoch(wd.getStartTime()));
        if (wd.getEndTime() != null)
            dao.setEndTime(DateConverter.toEpoch(wd.getEndTime()));
        return dao;
    }

    public WorkflowRun mapToEntity() {
        WorkflowRun wd = new WorkflowRun();
        if (this.endTime != null)
            wd.setEndTime(DateConverter.toLocalDateTime(this.endTime));
        if (this.startTime != null)
            wd.setStartTime(DateConverter.toLocalDateTime(this.startTime));
        wd.setRunId(this.runId);
        wd.setParams(Json.toJson(this.parameters));
        wd.setWorkflowDefinition(this.workflowDefinition.mapToEntity());
        return wd;
    }
}
