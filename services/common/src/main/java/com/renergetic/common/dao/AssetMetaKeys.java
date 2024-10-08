package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AssetMetaKeys {
    @JsonProperty(required = false, value = "categories")
    private List<AssetCategoryDAO> assetCategories;
    @JsonProperty(required = false, value = "types")
    private List<AssetTypeDAO> assetTypes;
    //TODO: make ir configurable - file or db
    @JsonProperty(required = false, value = "asset_details_keys")
    private List<String> assetDetailsKeys = List.of("threshold_heat_min", "threshold_heat_max", "threshold_electricity_min", "threshold_electricity_max", "color", "prediction_model", "threshold_renewable_percent");


    public AssetMetaKeys(List<AssetTypeDAO> assetTypes, List<AssetCategoryDAO> assetCategories) {
        this.assetCategories = assetCategories;
        this.assetTypes = assetTypes;
    }
}
