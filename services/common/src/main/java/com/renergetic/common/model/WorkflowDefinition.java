package com.renergetic.common.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "workflow_definition" )
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class WorkflowDefinition {

    @Column(name = "pipeline_id" )
    private String pipelineId;
    @Column(name = "name" )
    private String name;

    @Column( name = "visible")
    private Boolean visible=false;
    @OneToOne(cascade = CascadeType.DETACH )
    @JoinColumn(name = "current_run_id"  )
    private WorkflowRun workflowRun;
    @OneToMany(cascade = CascadeType.REFRESH, mappedBy = "workflowDefinition")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<WorkflowParameter> parameters;

    //TODO: maximum time for the task to finish?
    public  WorkflowDefinition(String pipelineId) {
       this.pipelineId=pipelineId;
    }


}
