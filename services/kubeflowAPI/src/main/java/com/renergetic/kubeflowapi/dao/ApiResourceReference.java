package com.renergetic.kubeflowapi.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@Entity
@AllArgsConstructor
//@Table(name = "api_resource_reference")
public class ApiResourceReference {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore()
    private Long id;
    
//    @OneToOne
//    @JoinColumn(name = "key_id", nullable = false, updatable = true)
    private ApiResourceKey key;

//    @Column(nullable = true, name = "name")
    private String name;

//    @Column(nullable = true, name = "relationship")
    private String relationship;

}