package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.hdrapi.model.AssetTypeCategory;
import com.renergetic.hdrapi.model.AssetType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "asset_type")
@RequiredArgsConstructor
@ToString
public class AssetTypeDAO {
    @Id
    @JsonProperty(required = true, access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(required = true, access = JsonProperty.Access.READ_ONLY)
    private String name;

    @JsonProperty(required = false, access = JsonProperty.Access.READ_ONLY)
    private String label;
//    @JsonProperty(value = "category", required = false)
//    private String category;

    public AssetTypeDAO(long id, String name, String label ) {
        super();
        this.id = id;
        this.name = name;
        this.label = label;
//        this.category = category;
    }


    public static AssetTypeDAO create(AssetType assetType) {
        AssetTypeDAO dao = null;

        if (assetType != null) {
            dao = new AssetTypeDAO();

            dao.setId(assetType.getId());
            dao.setName(assetType.getName());
            dao.setLabel(assetType.getLabel());
//            if (dao.getCategory() != null)
//                dao.setCategory(assetType.getTypeCategory().name());
        }
        return dao;
    }

    //	public AssetType(long id, String name, String label, AssetCategory category ) {
    public AssetType mapToEntity() {
        AssetType assetType = new AssetType();

        assetType.setId(id);
        assetType.setName(name);
        assetType.setLabel(label);
//        if (category != null) {
//            assetType.setTypeCategory(AssetTypeCategory.valueOf(category));
//        }
        return assetType;
    }
}
