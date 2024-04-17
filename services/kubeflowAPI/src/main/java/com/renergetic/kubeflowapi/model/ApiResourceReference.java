package com.renergetic.kubeflowapi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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