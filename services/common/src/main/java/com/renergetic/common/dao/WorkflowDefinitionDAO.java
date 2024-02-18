package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.WorkflowDefinition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WorkflowDefinitionDAO {

    @JsonProperty(required = true, value = "experiment_id")
    String experimentId;
    @JsonProperty(required = false, value = "name")
    String name;
    @JsonProperty(required = false)
    List<WorkflowPipelineDAO> pipelines;
    @JsonProperty(required = false, value = "parameters")
    Map<String, Object> parameters = Collections.emptyMap();
    @JsonProperty(required = true, value = "visible")
    boolean visible = false;
    @JsonProperty(required = false, value = "current_run")
    WorkflowRunDAO currentRun;

    public WorkflowDefinitionDAO(String experimentId) {
        this.experimentId = experimentId;
    }

    public static WorkflowDefinitionDAO create(WorkflowDefinition wd) {
        WorkflowDefinitionDAO dao = new WorkflowDefinitionDAO();
        dao.visible = wd.getVisible();
        dao.setExperimentId(wd.getExperimentId());
        if (wd.getWorkflowRun() != null) {
            dao.setCurrentRun(WorkflowRunDAO.create(wd.getWorkflowRun()));
        }
        return dao;
    }

    public WorkflowDefinition mapToEntity() {
        WorkflowDefinition wd = new WorkflowDefinition();
        wd.setExperimentId(this.experimentId);
        wd.setVisible(this.visible);
        if (this.currentRun != null)
            wd.setWorkflowRun(this.currentRun.mapToEntity());
        return wd;
    }
}
