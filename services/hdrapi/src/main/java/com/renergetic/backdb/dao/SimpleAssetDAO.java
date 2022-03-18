package com.renergetic.backdb.dao;

import com.renergetic.backdb.model.Asset;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class SimpleAssetDAO {
	private Long id;

	private String name;

	private Long type;

	private String label;

	private String description;

	private String geo_location;
	
	public static SimpleAssetDAO create(Asset asset) {
		SimpleAssetDAO dao = new SimpleAssetDAO();
		
		dao.setId(asset.getId());
		dao.setName(asset.getName());

		if (asset.getType() != null)
			dao.setType(asset.getType().getId());
		
		dao.setLabel(asset.getLabel());
		dao.setDescription(asset.getDescription());
		dao.setGeo_location(asset.getLocation());
		
		return dao;
	}
	
	public Asset mapToEntity() {
		Asset asset = new Asset();
		
		asset.setId(id);
		asset.setName(name);
		asset.setType(type);
		asset.setLabel(label);
		asset.setDescription(description);
		asset.setLocation(geo_location);

		return asset;
	}
}
