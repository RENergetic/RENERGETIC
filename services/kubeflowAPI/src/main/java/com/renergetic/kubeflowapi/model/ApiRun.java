package com.renergetic.kubeflowapi.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "run")
public class ApiRun {
    
    @Id
    @Column(nullable = true, name = "run_id")
    private String id;

    @Column(nullable = true, name = "name")
    private String name;

    @Column(nullable = true, name = "storage_state")
    private String storage_state; // x ∈ { STORAGESTATE_AVAILABLE (default) , STORAGESTATE_ARCHIVED }

    @Column(nullable = true, name = "description")
    private String description;

    @JoinColumn(name = "asset_type_id", nullable = false, insertable = true, updatable = true) //TODO
    private PipelineSpec pipeline_spec;

    @JoinColumn(name = "asset_type_id", nullable = false, insertable = true, updatable = true) //TODO
    private List<ApiResourceReference> resource_references;

    @Column(nullable = true, name = "run_service_account")
    private String service_account;

    @Column(nullable = true, name = "run_created_at")
    private String createt_at;

    @Column(nullable = true, name = "run_scheduled_at")
    private String scheduled_at;

    @Column(nullable = true, name = "run_finished_at")
    private String finished_at;

    @Column(nullable = true, name = "run_status")
    private String status;

    @Column(nullable = true, name = "run_error")
    private String error;

    @Column(nullable = true, name = "run_metrics") //Its really a string
    private Object[] metrics;

}