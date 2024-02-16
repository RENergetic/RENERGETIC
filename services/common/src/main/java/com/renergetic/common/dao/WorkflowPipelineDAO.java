package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.HDRRequest;
import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.common.utilities.Json;
import lombok.*;
import org.apache.tomcat.util.json.ParseException;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class WorkflowPipelineDAO {
    @JsonProperty(required = true)
    String id;
    @JsonProperty(required = false)
    String name;

    public WorkflowPipelineDAO(String id){
        this.id=id;
    }

}
