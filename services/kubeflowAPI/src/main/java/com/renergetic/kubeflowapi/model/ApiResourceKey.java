package com.renergetic.kubeflowapi.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResourceKey {

    private String id;

    private String type;

}