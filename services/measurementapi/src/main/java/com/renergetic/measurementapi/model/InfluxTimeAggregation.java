package com.renergetic.measurementapi.model;

import lombok.Getter;

public enum InfluxTimeAggregation {
    HOUR("1h"),
    DAY("1d"),
    MONTH("1m"),
    QUARTER("3m"),
    YEAR("1y");

    @Getter
    private String value;

    InfluxTimeAggregation(String value){
        this.value = value;
    }

    public static InfluxTimeAggregation obtain(String function) {
        try {
            return InfluxTimeAggregation.valueOf(function.toUpperCase());
        }catch (IllegalArgumentException e) {
            return null;
        }
    }
}
