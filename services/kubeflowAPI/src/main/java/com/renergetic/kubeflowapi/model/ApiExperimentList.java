package com.renergetic.kubeflowapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "experiments")
public class ApiExperimentList {
    
    @JoinColumn(name = "experiment_id", nullable = false, updatable = true) //TODO
    ApiExperiment[] experiments;

    @Column(nullable = true, name = "total_size")
    Integer total_size;

    @Column(nullable = true, name = "next_page_token")
    String next_page_token;
}
