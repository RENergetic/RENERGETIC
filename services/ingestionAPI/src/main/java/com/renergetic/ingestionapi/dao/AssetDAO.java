package com.renergetic.ingestionapi.dao;

import com.renergetic.ingestionapi.model.Asset;
import com.renergetic.ingestionapi.model.AssetType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class AssetDAO {
	private Long id;

	private String name;

	private AssetType type;

	private String label;

	private String description;

	private String geo_location;
	
	public static AssetDAO create(Asset asset) {
		AssetDAO dao = new AssetDAO();
		
		dao.setId(asset.getId());
		dao.setName(asset.getName());
		dao.setLabel(asset.getLabel());

		if(asset.getType() != null) dao.setType(asset.getType());
		
		dao.setGeo_location(asset.getLocation());
		
		return dao;
	}
	
	public Asset mapToEntity() {
		Asset asset = new Asset();
		
		asset.setId(id);
		asset.setName(name);
		asset.setLabel(label);
		asset.setType(type);
		asset.setLocation(geo_location);

		return asset;
	}
}
