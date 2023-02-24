package com.renergetic.measurementapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardExt {
    @JsonProperty(value = "measurement_type", required = false)
    @SerializedName("measurement_type")
    private String measurementType;
    @JsonProperty(value = "unit", required = false)
    @SerializedName("unit")
    private String unit;
}
