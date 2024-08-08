package com.renergetic.kpiapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.Domain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


public interface AbstractMeterIdentifier {
    String getName(); //-> change to type
    @JsonProperty(value = "custom_name")
    String getCustomName();
    String getDomain();



}
