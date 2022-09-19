package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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

	@JsonProperty(required = false)
	private Long asset_category;

	@JsonProperty(required = false)
	private List<Long> dashboards;
	
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

			if(asset.getAssetCategory() != null)
				dao.setAsset_category(asset.getAssetCategory().getId());

			if (asset.getAssetsDashboard() != null){
				List<Long> dashboardIds = new ArrayList<>();
				for(Dashboard dashboard : asset.getAssetsDashboard())
					dashboardIds.add(dashboard.getId());
				dao.setDashboards(dashboardIds);
			}
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
		if(asset_category != null){
			AssetCategory assetCategory = new AssetCategory();
			assetCategory.setId(asset_category);
			asset.setAssetCategory(assetCategory);
		}
		if(dashboards != null){
			List<Dashboard> dashboardsEntities = new ArrayList<>();
			for(long id : dashboards){
				Dashboard dashboard = new Dashboard();
				dashboard.setId(id);
				dashboardsEntities.add(dashboard);
			}
			asset.setAssetsDashboard(dashboardsEntities);
		}
		return asset;
	}
}
