package com.renergetic.kubeflowapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
