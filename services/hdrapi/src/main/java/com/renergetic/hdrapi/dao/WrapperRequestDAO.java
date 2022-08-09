package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class WrapperRequestDAO {
    @JsonProperty(required = true)
    private CallsWrapperRequestDAO calls;

    @Getter
    @Setter
    @RequiredArgsConstructor
    @ToString
    public static class CallsWrapperRequestDAO {
        @JsonProperty(required = false)
        private PaginationArgsWrapperRequestDAO assets;
        @JsonProperty(required = false, value = "asset_panels")
        private PaginationArgsWrapperRequestDAO assetPanels;
        @JsonProperty(required = false)
        private InfluxArgsWrapperRequestDAO data;
        @JsonProperty(required = false)
        private PaginationArgsWrapperRequestDAO demands;
        @JsonProperty(required = false)
        private PaginationArgsWrapperRequestDAO panels;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @ToString
    public static class PaginationArgsWrapperRequestDAO {
        @JsonProperty(required = false)
        private Long offset;
        @JsonProperty(required = false)
        private Integer limit;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @ToString
    public static class InfluxArgsWrapperRequestDAO {
        @JsonProperty(required = false)
        private String from;
        @JsonProperty(required = false)
        private String to;
        @JsonProperty(required = false)
        private String bucket;
// The wrapper API can return many measurements with different fields, for now, I propose use the name of measurement type as field
//        @JsonProperty(required = false)
//        private String field;
        @JsonProperty(required = false)
        private Map<String, String> tags;
    }
}
