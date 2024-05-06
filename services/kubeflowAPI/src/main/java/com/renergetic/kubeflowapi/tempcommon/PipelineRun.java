package com.renergetic.kubeflowapi.tempcommon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pipeline_run")

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class PipelineRun {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "workflow_run_id")
//    private Long workflowRunId;
    @Id
    @Column(name = "run_id",nullable = true)
    private String runId;
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "pipeline_id")
    private PipelineDefinition pipelineDefinition;
    //Run paramaters
    @Column(name = "params", columnDefinition = "TEXT")
    private String params;
    @Column(nullable = true, name = "start_time")
    private LocalDateTime startTime;
    @Column(nullable = true, name = "end_time")
    private LocalDateTime endTime;
    @Column(nullable = true, name = "state")
    private String state;

}
