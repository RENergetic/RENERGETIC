package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InformationTileDAORequest {
    @JsonProperty()
    private Long id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty()
    private String label;

    @JsonProperty()
    private Long type;
    
    @JsonProperty()
    private Boolean featured;

    @JsonProperty(required = true, value = "panel_id")
    private Long panelId;

    @JsonProperty()
    private String layout;

    @JsonProperty()
    private String props;

    @JsonProperty()
    private List<MeasurementDAORequest> measurements;
}
