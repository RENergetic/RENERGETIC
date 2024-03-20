package com.renergetic.kubeflowapi.dao.tempcommon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.common.utilities.Json;
import jakarta.persistence.*;
import lombok.*;
import org.apache.tomcat.util.json.ParseException;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.Map;


@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
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
