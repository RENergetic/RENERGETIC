package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "pipeline_definition" )
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class PipelineDefinition {

    @Id
    @Column(name = "id",unique = true )
    private String pipelineId;
    @Column(name = "name")
    private String name = "<name>";
    @Column(name = "label" )
    private String label;
//    @Column(name = "update_date" )
//    private String  updateDate;

    @Column( name = "visible")
    private Boolean visible=false;
    @OneToOne(cascade = CascadeType.DETACH )
    @JoinColumn(name = "current_run_id"  )
    private PipelineRun pipelineRun;
    @OneToMany(cascade = CascadeType.REFRESH, mappedBy = "pipelineDefinition")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<PipelineParameter> parameters;

    @OneToMany(cascade = CascadeType.REFRESH, mappedBy = "pipelineDefinition")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<PipelineDefinitionProperty> properties;

    @ManyToOne
    @JoinColumn(name = "information_panel_id",nullable = true)
    private InformationPanel informationPanel;

    //TODO: maximum time for the task to finish?
    public  PipelineDefinition(String pipelineId) {
       this.pipelineId=pipelineId;
    }


}
