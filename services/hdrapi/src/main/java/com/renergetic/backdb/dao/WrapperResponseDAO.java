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
public class WrapperResponseDAO {
    @JsonProperty(required = false)
    private List<SimpleAssetDAO> assets;

    @JsonProperty(required = false, value = "asset_panels")
    private List<AssetPanelDAO> assetPanels;

    @JsonProperty(required = false)
    private DataDAO data;

    @JsonProperty(required = false)
    private List<DemandScheduleDAO> demands;

    @JsonProperty(required = false)
    private List<InformationPanelDAOResponse> panels;
}
