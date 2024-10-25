package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.PipelineDefinition;
import com.renergetic.common.model.PipelineRun;
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
public class PipelineRunDAO {

    @JsonProperty(required = true, value = "run_id")
    String runId;
    @JsonProperty(required = true, value = "pipeline")
    PipelineDefinitionDAO pipelineDefinitionDAO;
    @JsonProperty(required = false, value = "name")
    String name;
    //run parameters
    @JsonProperty(required = true, value = "parameters")
    Map<String, Object> parameters = Collections.emptyMap();

    @JsonProperty(required = false, value = "init_time")
    private Long initTime;
    @JsonProperty(required = false, value = "start_time")
    private Long startTime;
    @JsonProperty(required = false, value = "end_time")
    private Long endTime;
    @JsonProperty(required = false, value = "state")
    private String state;

    public PipelineRunDAO(String runId, PipelineDefinitionDAO pipelineDefinitionDAO) {
        this.runId = runId;
        this.pipelineDefinitionDAO = pipelineDefinitionDAO;
    }

    public static PipelineRunDAO create(PipelineRun wd) {
        PipelineRunDAO dao = new PipelineRunDAO();
        dao.setRunId(wd.getRunId());
        if (wd.getPipelineDefinition() != null) {
            PipelineDefinitionDAO pipelineDefinitionDAO = new PipelineDefinitionDAO();
            pipelineDefinitionDAO.setPipelineId(wd.getPipelineDefinition().getPipelineId());
            pipelineDefinitionDAO.setName(wd.getPipelineDefinition().getName());
            dao.setPipelineDefinitionDAO(pipelineDefinitionDAO);
        }
        dao.setState(wd.getState());
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
            dao.setStartTime(wd.getStartTime());
        if (wd.getEndTime() != null)
            dao.setEndTime(wd.getEndTime());
        dao.setInitTime(wd.getInitTime());
        dao.setName(wd.getName());
        return dao;
    }

    public PipelineRun mapToEntity() {
        PipelineRun wd = new PipelineRun();
        if (this.endTime != null)
            wd.setEndTime(this.endTime);
        if (this.startTime != null)
            wd.setStartTime(this.startTime);
        wd.setRunId(this.runId);
        wd.setState(this.state);
        wd.setParams(Json.toJson(this.parameters));
        wd.setPipelineDefinition(this.pipelineDefinitionDAO.mapToEntity());
        wd.setInitTime(this.initTime);
        wd.setName(name);
        return wd;
    }
}
