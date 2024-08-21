package com.renergetic.kubeflowapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;


/**
 * Kubeflow resource key (id)
 */
@AllArgsConstructor
public class ApiResourceKey {


    @JsonProperty(required = true, value = "id")
    @SerializedName( "id")
    private String keyId;

    /**
     * object's type
     */
    @JsonProperty(required = true, value = "type")
    @SerializedName( "type")
    private String type;

}