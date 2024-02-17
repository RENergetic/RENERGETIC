package com.renergetic.kubeflowapi.dao.tempcommon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;



@Entity
@Table(name = "workflow_definition" )

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class WorkflowDefinition {
    @Id
    @Column(name = "experiment_id" )
    private String experimentId;

    @Column( name = "visible")
    private Boolean visible=false;
    public  WorkflowDefinition(String experimentId) {
       this.experimentId=experimentId;
    }


}
