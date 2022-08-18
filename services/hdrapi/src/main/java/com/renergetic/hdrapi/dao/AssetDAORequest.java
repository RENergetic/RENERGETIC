package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.AssetType;
import com.renergetic.hdrapi.model.User;

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
	private Long type;

	@JsonProperty(required = false)
	private String label;

	@JsonProperty(required = false)
	private String description;

	@JsonProperty(required = false)
	private String geo_location;

	@JsonProperty(required = false)
	private Long parent;

	@JsonProperty(required = false)
	private Long user;
	
	public static AssetDAORequest create(Asset asset) {
		AssetDAORequest dao = null;
		
		if (asset != null) {
			dao = new AssetDAORequest();
		
			dao.setId(asset.getId());
			dao.setName(asset.getName());
			
			if (asset.getType() != null)
				dao.setType(asset.getType().getId());
			
			dao.setLabel(asset.getLabel());
			dao.setGeo_location(asset.getLocation());
			
			if (asset.getParentAsset() != null) 
				dao.setParent(asset.getParentAsset().getId());
			
			if (asset.getUser() != null) 
				dao.setUser(asset.getUser().getId());
		}
		return dao;
	}
	
	public Asset mapToEntity() {
		Asset asset = new Asset();
		
		asset.setId(id);
		asset.setName(name);
		
		if (type != null) {
			AssetType entityType = new AssetType();
			entityType.setId(type);
			asset.setType(entityType);
		}
		asset.setLabel(label);
		asset.setLocation(geo_location);
		
		if (parent != null) {
			Asset entityParent = new Asset();
			entityParent.setId(parent);
			asset.setParentAsset(entityParent);
		}
		if (user != null) {
			User entityUser = new User();
			entityUser.setId(user);
			asset.setUser(entityUser);
		}
		return asset;
	}
}