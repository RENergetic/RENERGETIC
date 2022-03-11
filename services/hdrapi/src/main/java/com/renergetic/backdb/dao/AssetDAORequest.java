package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Asset;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class AssetDAORequest {
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;

	@JsonProperty(required = true)
	private String name;

	@JsonProperty(required = true)
	private String type;

	@JsonProperty(required = false)
	private String label;

	@JsonProperty(required = false)
	private String description;

	@JsonProperty(required = false)
	private String geo_location;

	@JsonProperty(required = false)
	private Long parent;

	@JsonProperty(required = false)
	private Long owner;
	
	public static AssetDAORequest create(Asset asset) {
		AssetDAORequest dao = null;
		
		if (asset != null) {
			dao = new AssetDAORequest();
		
			dao.setId(asset.getId());
			dao.setName(asset.getName());
			dao.setType(asset.getType());
			dao.setLabel(asset.getLabel());
			dao.setDescription(asset.getDescription());
			dao.setGeo_location(asset.getLocation());
			
			if (asset.getParentAsset() != null) 
				dao.setParent(asset.getParentAsset().getId());
			
			if (asset.getOwner() != null) 
				dao.setOwner(asset.getOwner().getId());
		}
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
		asset.setParentAsset(parent);
		asset.setOwner(owner);
		
		return asset;
	}
}