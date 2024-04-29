package com.renergetic.common.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "workflow_run")

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class WorkflowRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workflow_run_id")
    private Long workflowRunId;

    @Column(name = "run_id",nullable = true)
    private String runId;
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "pipeline_id")
    private WorkflowDefinition workflowDefinition;
    @Column(name = "params", columnDefinition = "TEXT")
    private String params;
    @Column(nullable = true, name = "start_time")
    private LocalDateTime startTime;
    @Column(nullable = true, name = "end_time")
    private LocalDateTime endTime;

}
