package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Area;
import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Dashboard;
import com.renergetic.backdb.model.Heatmap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class AreaDAO {	
	@JsonProperty(required = true, access = Access.READ_ONLY)
	private Long id;

	@JsonProperty(required = true)
	private String name;

	@JsonProperty(required = false)
	private String label;

	@JsonProperty(required = true)
	private String roi;

	@JsonProperty(value = "geo_id", required = false)
	private String geoId;

	@JsonProperty(value = "asset_id", required = false)
	private Long asset;

	@JsonProperty(value = "parent_heatmap_id", required = false)
	private Long parent;

	@JsonProperty(value = "child_heatmap_id", required = false)
	private Long child;

	@JsonProperty(value = "dashboard_id", required = false)
	private Long dashboard;
	
	public static AreaDAO create(Area area) {
		AreaDAO dao = null;
		
		if (area != null) {
			dao = new AreaDAO();
		
			dao.setId(area.getId());
			dao.setName(area.getName());
			dao.setLabel(area.getLabel());
			dao.setRoi(area.getRoi());
			dao.setGeoId(area.getGeoId());
			
			if (area.getAsset() != null)
				dao.setAsset(area.getAsset().getId());
			
			if (area.getChild() != null)
				dao.setChild(area.getChild().getId());
			
			if (area.getParent() != null)
				dao.setParent(area.getParent().getId());
			
			if (area.getDashboard() != null) 
				dao.setDashboard(area.getDashboard().getId());
		}
		return dao;
	}
	
	public Area mapToEntity() {
		Area area = new Area();
		
		area = new Area();
	
		area.setId(area.getId());
		area.setName(area.getName());
		area.setLabel(area.getLabel());
		area.setRoi(area.getRoi());
		area.setGeoId(area.getGeoId());
		
		if (asset != null) {
			Asset asset = new Asset();
			asset.setId(asset.getId());
			area.setAsset(asset);
		}
		if (child != null) {
			Heatmap childEntity = new Heatmap();
			childEntity.setId(child);
			area.setChild(childEntity);
		}
		if (parent != null) {
			Heatmap parentEntity = new Heatmap();
			parentEntity.setId(parent);
			area.setParent(parentEntity);
		}
		if (dashboard != null) {
			Dashboard dashboardEntity = new Dashboard();
			dashboardEntity.setId(dashboard);
			area.setDashboard(dashboardEntity);
		}
		return area;
	}
	
}
