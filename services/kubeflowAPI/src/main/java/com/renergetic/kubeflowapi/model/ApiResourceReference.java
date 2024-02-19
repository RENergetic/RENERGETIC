package com.renergetic.kubeflowapi.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResourceReference {

    private ApiResourceKey key;

    private String name;

    private String relationship;

}