package com.renergetic.hdrapi.dao.tempcommon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
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
