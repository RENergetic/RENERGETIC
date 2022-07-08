package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
        private PaginationArgsWrapperRequestDAO data;
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
}
