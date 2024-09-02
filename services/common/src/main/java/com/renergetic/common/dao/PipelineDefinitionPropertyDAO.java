package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.PipelineDefinitionProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class PipelineDefinitionPropertyDAO {


    @JsonProperty(required = true)
    String key;

    @JsonProperty(required = false)
    String value;

    @JsonProperty(required = false)
    String type;

    @JsonProperty(required = false)
    String description;

    public PipelineDefinitionPropertyDAO(String key, String value,String type, String description) {
        this.key = key;
        this.type = type;
        this.value=value;
        this.description = description;
    }

    public static PipelineDefinitionPropertyDAO create(PipelineDefinitionProperty wp) {
        PipelineDefinitionPropertyDAO dao = new PipelineDefinitionPropertyDAO();
        dao.setKey(wp.getKey());
        dao.setType(wp.getType());
        dao.setValue(wp.getValue());
        dao.setDescription(wp.getDescription());
        return dao;
    }

    public PipelineDefinitionProperty mapToEntity() {

        PipelineDefinitionProperty wp = new PipelineDefinitionProperty();
        wp.setKey(this.getKey());
        wp.setType(this.getType());
        wp.setValue(this.getValue());
        wp.setDescription(this.getDescription());
        return wp;
    }

}
