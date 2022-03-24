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
public class InformationTileDAOResponse {
    @JsonProperty()
    private Long id;

    @JsonProperty(required = true)
    private String title;

    @JsonProperty
    private String type;

    @JsonProperty()
    private List<MeasurementDAOResponse> measurements;

    @JsonProperty
    private String props;

    @JsonProperty
    private String layout;
}
