/*package com.renergetic.kubeflowapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

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
*/