package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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
    @OneToOne(cascade = CascadeType.DETACH )
    @JoinColumn(name = "current_run_id"  )
    private WorkflowRun workflowRun;

    //TODO: maximum time for the task to finish?
    public  WorkflowDefinition(String experimentId) {
       this.experimentId=experimentId;
    }


}
