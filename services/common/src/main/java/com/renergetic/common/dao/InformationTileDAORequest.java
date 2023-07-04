package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.InformationTileType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InformationTileDAORequest {
    @JsonProperty()
    private Long id;

    @JsonProperty( )
    private String name;

    @JsonProperty()
    private String label;

    @JsonProperty()
    private InformationTileType type;

    @JsonProperty(required = true, value = "panel_id")
    private Long panelId;

    @JsonProperty()
    private String layout;

    @JsonProperty()
    private String props;
}
