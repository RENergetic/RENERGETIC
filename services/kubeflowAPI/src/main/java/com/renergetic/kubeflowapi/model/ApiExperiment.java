package com.renergetic.kubeflowapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "experiment")
public class ApiExperiment {
    
    @Id
    @Column(nullable = true, name = "experiment_id")
    String experiment_id;

    @Column(nullable = true, name = "name")
    String name;

    @Column(nullable = true, name = "description")
    String description;

    @Column(nullable = true, name = "created_at")
    String createdAt;

    @OneToOne
    @JoinColumn(name = "key_id", nullable = false, updatable = true)
    ApiResourceReference resource_reference;

    @Column(nullable = true, name = "storage_state")
    String storage_state;
}
