package com.renergetic.hdrapi.dao.tempcommon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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


}
