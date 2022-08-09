package com.renergetic.hdrapi.dao;

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
	private Long id;

	private String name;

	private String type;

	private String label;

	private String description;

	private String geo_location;
	
	public static SimpleAssetDAO create(Asset asset) {
		SimpleAssetDAO dao = new SimpleAssetDAO();
		
		dao.setId(asset.getId());
		dao.setName(asset.getName());

		if (asset.getType() != null) {
			if(asset.getType().getLabel() != null) dao.setType(asset.getType().getLabel());
			else  dao.setType(asset.getType().getName());
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
