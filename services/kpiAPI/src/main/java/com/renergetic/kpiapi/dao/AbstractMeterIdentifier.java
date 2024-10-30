package com.renergetic.kpiapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface AbstractMeterIdentifier {
    
    String getName(); //-> change to type

    @JsonProperty(value = "custom_name")
    String getCustomName();

    String getDomain();

}
