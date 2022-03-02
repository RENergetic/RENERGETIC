package com.renergetic.backdb.dao;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Measurement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class AssetDAO {
	@Getter
	@Setter
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;

	@Getter
	@Setter
	@JsonProperty(required = true)
	private String name;

	@Getter
	@Setter
	@JsonProperty(required = true)
	private String type;

	@Getter
	@Setter
	@JsonProperty(required = false)
	private String label;

	@Getter
	@Setter
	@JsonProperty(required = false)
	private String description;

	@Getter
	@Setter
	@JsonProperty(required = false)
	private String geo_location;

	@Getter
	@JsonProperty(required = false)
	private SimpleAssetDAO parent;

	@Getter
	@Setter
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private List<SimpleAssetDAO> child;

	@Getter
	@Setter
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private List<SimpleMeasurementDAO> measuremets;

	@Getter
	@Setter
	@JsonProperty(required = false)
	private Long owner;
	
	public void setParent(Long parent_id) {
		this.parent = new SimpleAssetDAO();
		this.parent.setId(parent_id);
	}
	
	public void parent(SimpleAssetDAO parent) {
		this.parent = parent;
	}
	
	public static AssetDAO create(Asset asset, List<Asset> childs, List<Measurement> measurements) {
		AssetDAO dao = new AssetDAO();
		
		dao.setId(asset.getId());
		dao.setName(asset.getName());
		dao.setType(asset.getType());
		dao.setLabel(asset.getLabel());
		dao.setDescription(asset.getDescription());
		dao.setGeo_location(asset.getLocation());
		
		if (asset.getParentAsset() != null) 
			dao.parent(SimpleAssetDAO.create(asset.getParentAsset()));
		
		if (childs != null) {
			List<SimpleAssetDAO> mapChilds = new ArrayList<>();
			for (Asset child : childs)
				mapChilds.add(SimpleAssetDAO.create(child));
			dao.setChild(mapChilds);
		}
		if (measurements != null) {
			List<SimpleMeasurementDAO> mapMeasurements = new ArrayList<>();
			for (Measurement measurement : measurements)
				mapMeasurements.add(SimpleMeasurementDAO.create(measurement));
			dao.setMeasuremets(mapMeasurements);
		}
		if (asset.getOwner() != null) 
			dao.setOwner(asset.getOwner().getId());
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
		if (parent == null) asset.setParentAsset(null);
		else asset.setParentAsset(parent.getId());
		asset.setOwner(owner);
		
		return asset;
	}
}
