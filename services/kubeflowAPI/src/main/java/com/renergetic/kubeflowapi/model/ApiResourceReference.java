package com.renergetic.kubeflowapi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
@Table(name = "Api_reresource_reference")
public class ApiResourceReference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore()
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "key_id", nullable = false, updatable = true)
    private ApiResourceKey key;

    @Column(nullable = true, name = "name")
    private String name;

    @Column(nullable = true, name = "relationship")
    private String relationship;

}