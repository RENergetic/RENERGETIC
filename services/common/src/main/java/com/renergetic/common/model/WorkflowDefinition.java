package com.renergetic.common.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
    @OneToMany(cascade = CascadeType.REFRESH, mappedBy = "workflowDefinition")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<WorkflowParameter> parameters;

    //TODO: maximum time for the task to finish?
    public  WorkflowDefinition(String experimentId) {
       this.experimentId=experimentId;
    }


}
