package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.hdrapi.model.Asset;

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
    @JsonProperty()
    private Long id;

    @JsonProperty()
    private String name;
    //    @JsonProperty()
//    private SimpleAssetDAO parent;
//    @JsonProperty()
    //TODO:? name can be discussed :)
    //assets children , its different than connections
    //childs and parent should be shared in this class because infinite loops appears at AssetDAOResponse class
//    private List<SimpleAssetDAO> child = new ArrayList<>();
    @JsonProperty()
    private AssetTypeDAO type;

    @JsonProperty()
    private String label;

    @JsonProperty()
    private String description;

    @JsonProperty(value = "geo_location")
    private String geoLocation;

    public static SimpleAssetDAO create(Asset asset) {
        if (asset == null)
            return null;
        SimpleAssetDAO dao = new SimpleAssetDAO();

        dao.setId(asset.getId());
        dao.setName(asset.getName());
//        if(asset.getParentAsset()!=null){
//            dao.setParent(SimpleAssetDAO.create(asset.getParentAsset()));
//        }

        if (asset.getType() != null) {
            AssetTypeDAO assetTypeDAO = AssetTypeDAO.create(asset.getType());
            dao.setType(assetTypeDAO);
//			if(asset.getType().getLabel() != null) dao.setType(asset.getType().getLabel());
//			else  dao.setType(asset.getType().getName());
        }

        dao.setLabel(asset.getLabel());
        //dao.setDescription(asset.getDescription());
        dao.setGeoLocation(asset.getLocation());

        return dao;
    }

    public Asset mapToEntity() {
        Asset asset = new Asset();
        asset.setId(id);
        asset.setName(name);
        if (type != null)
            asset.setType(type.mapToEntity());
        asset.setLabel(label);
        //asset.setDescription(description);
        asset.setLocation(geoLocation);

        return asset;
    }
}
