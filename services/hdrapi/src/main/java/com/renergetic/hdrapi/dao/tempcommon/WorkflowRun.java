package com.renergetic.hdrapi.dao.tempcommon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_run" )

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class WorkflowRun {
    @Id
    @Column(name = "run_id" )
    private String runId;
    @ManyToOne(cascade = CascadeType.REFRESH )
    @JoinColumn(name = "experiment_id" )
    private WorkflowDefinition experimentId;
    @Column(name = "params",  columnDefinition="TEXT" )
    private String params;
    @Column(nullable = true,name = "start_time" )
    private LocalDateTime startTime;
    @Column(nullable = true,name = "end_time" )
    private LocalDateTime endTime;

}
