package com.renergetic.kubeflowapi.dao;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
//@Entity
//@Table(name = "pipeline_param")
public class KeyValue implements Serializable {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore()
    private Long id;

//    @Column(nullable = true, name = "name")
    @JsonProperty(required = true, value = "name")
    private String name;

//    @Column(nullable = true, name = "value")
    @JsonProperty(required = true, value = "value")
    private String value;

    public KeyValue(String name, String value) {
        this.name = name;
        this.value = value;
    }
}