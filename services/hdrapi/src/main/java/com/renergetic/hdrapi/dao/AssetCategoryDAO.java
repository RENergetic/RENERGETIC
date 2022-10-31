package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.hdrapi.model.AssetCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class AssetCategoryDAO {
    @JsonProperty(required = false)
    private Long id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = false)
    private String label;

    @JsonProperty(required = true)
    private String description;

    public static AssetCategoryDAO create(AssetCategory assetCategory){
        AssetCategoryDAO assetCategoryDAO = new AssetCategoryDAO();
        assetCategoryDAO.setId(assetCategory.getId());
        assetCategoryDAO.setName(assetCategory.getName());
        assetCategoryDAO.setLabel(assetCategory.getLabel());
        assetCategoryDAO.setDescription(assetCategory.getDescription());

        return assetCategoryDAO;
    }

    public AssetCategory mapToEntity(){
        AssetCategory assetCategory = new AssetCategory();
        assetCategory.setId(id);
        assetCategory.setName(name);
        assetCategory.setLabel(label);
        assetCategory.setDescription(description);
        return assetCategory;
    }
}
