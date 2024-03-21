package com.renergetic.kubeflowapi.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResourceReference {

    @OneToOne
    @JoinColumn(name = "key_id", nullable = false, updatable = true)
    private ApiResourceKey key;

    @Column(nullable = true, name = "name")
    private String name;

    @Column(nullable = true, name = "relationship")
    private String relationship;

}