package com.renergetic.kubeflowapi.tempcommon;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PipelineDefinitionDAO {

    @JsonProperty(required = true, value = "pipeline_id")
    String pipelineId;
    @JsonProperty(required = false, value = "name")
    String name;
    @JsonProperty(required = false, value = "description")
    String description;
    @JsonProperty(required = false, value = "version")
    String version;
    @JsonProperty(required = false, value = "parameters")
    Map<String, PipelineParameterDAO> parameters = Collections.emptyMap();
    @JsonProperty(required = true, value = "visible")
    boolean visible = false;
    @JsonProperty(required = false, value = "current_run")
    PipelineRunDAO currentRun;

    public PipelineDefinitionDAO(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public static PipelineDefinitionDAO create(PipelineDefinition wd) {
        PipelineDefinitionDAO dao = new PipelineDefinitionDAO();
        dao.setVisible(wd.getVisible());
        dao.setPipelineId(wd.getPipelineId());
        dao.setName(wd.getName());
        if (wd.getPipelineRun() != null) {
            dao.setCurrentRun(PipelineRunDAO.create(wd.getPipelineRun()));
        }
        if (wd.getParameters() != null) {
            Map<String, PipelineParameterDAO> map =
                    wd.getParameters().stream().map(PipelineParameterDAO::create)
                            .collect(Collectors.toMap(PipelineParameterDAO::getKey, it -> it));
            dao.setParameters(map);
        } else {
            dao.setParameters(Collections.emptyMap());
        }
        return dao;
    }

    public PipelineDefinition mapToEntity() {
        PipelineDefinition wd = new PipelineDefinition();
        wd.setPipelineId(this.pipelineId);
        wd.setVisible(this.visible);
        wd.setName(this.name);
        if (this.currentRun != null)
            wd.setPipelineRun(this.currentRun.mapToEntity());
        List<PipelineParameter> params =
                this.getParameters().values().stream().map(PipelineParameterDAO::mapToEntity).collect(
                        Collectors.toList());
        wd.setParameters(params);
        return wd;
    }
}
