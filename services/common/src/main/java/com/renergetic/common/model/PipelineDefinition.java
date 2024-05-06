package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "pipeline_definition" )
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class PipelineDefinition {

    @Id
    @Column(name = "id" )
    private String pipelineId;
    @Column(name = "name" )
    private String name;

    @Column( name = "visible")
    private Boolean visible=false;
    @OneToOne(cascade = CascadeType.DETACH )
    @JoinColumn(name = "current_run_id"  )
    private PipelineRun pipelineRun;
    @OneToMany(cascade = CascadeType.REFRESH, mappedBy = "pipelineDefinition")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<PipelineParameter> parameters;

    //TODO: maximum time for the task to finish?
    public  PipelineDefinition(String pipelineId) {
       this.pipelineId=pipelineId;
    }


}
