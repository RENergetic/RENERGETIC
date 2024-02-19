package com.renergetic.kubeflowapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyValueParam {

    String name;

    String value;
    
}