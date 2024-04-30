package com.renergetic.kubeflowapi.tempcommon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class PipelineParameterDAO {


    @JsonProperty(required = true)
    String key;

    @JsonProperty(required = false)
    String defaultValue;
    @JsonProperty(required = true)
    String type;
    @JsonProperty(required = true)
    Boolean visible = false;

    @JsonProperty(required = true)
    String description;

    public PipelineParameterDAO(String key, String defaultValue, String type, Boolean visible, String description) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
        this.description = description;
        this.visible = visible;
    }

    public static PipelineParameterDAO create(PipelineParameter wp) {
        PipelineParameterDAO dao = new PipelineParameterDAO();
        dao.setKey(wp.getKey());
        dao.setType(wp.getType());
        dao.setVisible(wp.getVisible());
        dao.setDefaultValue(wp.getDefaultValue());
        dao.setDescription(wp.getParameterDescription());
        return dao;
    }

    public PipelineParameter mapToEntity() {

        PipelineParameter wp = new PipelineParameter();
        wp.setKey(this.getKey());
        wp.setType(this.getType());
        wp.setVisible(this.getVisible());
        wp.setDefaultValue(this.getDefaultValue());
        wp.setParameterDescription(this.getDescription());
        return wp;
    }

}
