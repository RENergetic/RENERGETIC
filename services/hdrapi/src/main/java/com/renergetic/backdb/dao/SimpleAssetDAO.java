package com.renergetic.backdb.dao;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.AssetType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class SimpleAssetDAO {
    //TODO: query DB for all child assets  (inferred from parent id)
    //TODO: add parent asset reference here ->  it's just an idea ???
    private Long id;

    private String name;

    private AssetTypeDAO type;

    private String label;

    private String description;

    private String geo_location;

    public static SimpleAssetDAO create(Asset asset) {
        SimpleAssetDAO dao = new SimpleAssetDAO();

        dao.setId(asset.getId());
        dao.setName(asset.getName());

        if (asset.getType() != null) {
            AssetTypeDAO assetTypeDAO = AssetTypeDAO.create(asset.getType());
            dao.setType(assetTypeDAO);
//			if(asset.getType().getLabel() != null) dao.setType(asset.getType().getLabel());
//			else  dao.setType(asset.getType().getName());
        }

        dao.setLabel(asset.getLabel());
        //dao.setDescription(asset.getDescription());
        dao.setGeo_location(asset.getLocation());

        return dao;
    }

    public Asset mapToEntity() {
        Asset asset = new Asset();
        asset.setId(id);
        asset.setName(name);
        asset.setLabel(label);
        //asset.setDescription(description);
        asset.setLocation(geo_location);

        return asset;
    }
}
