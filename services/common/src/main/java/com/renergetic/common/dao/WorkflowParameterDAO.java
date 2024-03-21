package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class WorkflowParameterDAO {


    @JsonProperty(required = true)
    String key;

    @JsonProperty(required = false)
    String defaultValue;
    @JsonProperty(required = true)
    String type;

    @JsonProperty(required = true)
    String description;

    public WorkflowParameterDAO(String key, String defaultValue, String type, String description) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
        this.description = description;
    }

    public static WorkflowParameterDAO create(WorkflowParameter wp) {
        WorkflowParameterDAO dao = new WorkflowParameterDAO();
        dao.setKey(wp.getKey());
        dao.setType(wp.getType());
        dao.setDefaultValue(wp.getDefaultValue());
        dao.setDescription(wp.getParameterDescription());
        return dao;
    }

    public WorkflowParameter mapToEntity() {

        WorkflowParameter wp = new WorkflowParameter();
        wp.setKey(this.getKey());
        wp.setType(this.getType());
        wp.setDefaultValue(this.getDefaultValue());
        wp.setParameterDescription(this.getDescription());
        return wp;
    }

}
