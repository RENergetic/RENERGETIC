package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.WorkflowDefinition;
import com.renergetic.common.model.WorkflowParameter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    Map<String, WorkflowParameterDAO> parameters = Collections.emptyMap();
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
        if (wd.getParameters() != null) {
            Map<String, WorkflowParameterDAO> map =
                    wd.getParameters().stream().map(it -> WorkflowParameterDAO.create(it))
                            .collect(Collectors.toMap(WorkflowParameterDAO::getKey, it -> it));
            dao.setParameters(map);
        } else {
            dao.setParameters(Collections.emptyMap());
        }
        return dao;
    }

    public WorkflowDefinition mapToEntity() {
        WorkflowDefinition wd = new WorkflowDefinition();
        wd.setExperimentId(this.experimentId);
        wd.setVisible(this.visible);
        if (this.currentRun != null)
            wd.setWorkflowRun(this.currentRun.mapToEntity());
        List<WorkflowParameter> params =
                this.getParameters().values().stream().map(WorkflowParameterDAO::mapToEntity).collect(
                        Collectors.toList());
        wd.setParameters(params);
        return wd;
    }
}
