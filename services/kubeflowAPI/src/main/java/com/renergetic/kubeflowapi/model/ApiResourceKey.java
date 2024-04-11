package com.renergetic.kubeflowapi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@Table(name = "kubeflow_api_resource_key")
@AllArgsConstructor
public class ApiResourceKey {

    @Id
    @Column(nullable = true, name = "key_id")
    @JsonProperty(required = true, value = "key_id")
    private String keyId;

    @Column(nullable = true, name = "type")
    @JsonProperty(required = true, value = "type")
    private String type;

}