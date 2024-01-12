package com.renergetic.kubeflowapi.model;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
@Table(name = "example")
public class ExampleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, insertable = true, updatable = true, unique = true)
    private String name;

    @Column(name = "data", nullable = false, insertable = true, updatable = true, unique = false)
    private String data;
}
