package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.AssetType;
import com.renergetic.common.model.ConnectionType;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.details.AssetDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class AssetDAOResponse {
	@JsonProperty(required = false)
	private Long id;

	@JsonProperty(required = true)
	private String name;

	@JsonProperty(required = true)
	private AssetType type;

	@JsonProperty(required = false)
	private String label;

	@JsonProperty(required = false)
	private String geo_location;

	@JsonProperty(required = false)
	private SimpleAssetDAO parent;

	@JsonProperty(required = false)
	private List<SimpleAssetDAO> child;

	@JsonProperty(required = false)
	private List<SimpleMeasurementDAO> measurements;

	@JsonProperty(required = false)
	private Long user;

	@JsonProperty(required = false)
	private AssetCategoryDAO asset_category;
	
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Map<String, String> details;
	
	// Only used to asset_connection queries
	@JsonInclude(value = Include.NON_NULL)
	@JsonProperty(value = "connection_type", access = Access.READ_ONLY)
	private ConnectionType connectionType;
	
	public static AssetDAOResponse create(Asset asset, List<Asset> childs, List<Measurement> measurements) {
		AssetDAOResponse dao = null;
		
		if (asset != null) {
			dao = new AssetDAOResponse();
			
			dao.setId(asset.getId());
			dao.setName(asset.getName());
			dao.setType(asset.getType());
			dao.setLabel(asset.getLabel());
			dao.setGeo_location(asset.getLocation());
			
			if (asset.getParentAsset() != null) 
				dao.setParent(SimpleAssetDAO.create(asset.getParentAsset()));
			
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
				dao.setMeasurements(mapMeasurements);
			}
			if (asset.getUser() != null) 
				dao.setUser(asset.getUser().getId());
			if(asset.getAssetCategory() != null)
				dao.setAsset_category(AssetCategoryDAO.create(asset.getAssetCategory()));
			if(asset.getDetails() != null)
				dao.setDetails(asset.getDetails().stream().collect(
						HashMap::new, (m, p) -> m.put(p.getKey(),p.getValue()), Map::putAll));
						//Collectors.toMap(AssetDetails::getKey, AssetDetails::getValue)));
		}
		return dao;
	}
	public static AssetDAOResponse create(Asset asset, List<AssetDetails> measurements) {
		AssetDAOResponse dao = null;
		
		if (asset != null) {
			dao = new AssetDAOResponse();
			
			dao.setId(asset.getId());
			dao.setName(asset.getName());
			dao.setType(asset.getType());
			dao.setLabel(asset.getLabel());
			dao.setGeo_location(asset.getLocation());
			
			if (asset.getParentAsset() != null) 
				dao.setParent(SimpleAssetDAO.create(asset.getParentAsset()));
			
//			if (childs != null) {
//				List<SimpleAssetDAO> mapChilds = new ArrayList<>();
//				for (Asset child : childs)
//					mapChilds.add(SimpleAssetDAO.create(child));
//				dao.setChild(mapChilds);
//			}
//			if (measurements != null) {
//				List<SimpleMeasurementDAO> mapMeasurements = new ArrayList<>();
//				for (Measurement measurement : measurements)
//					mapMeasurements.add(SimpleMeasurementDAO.create(measurement));
//				dao.setMeasurements(mapMeasurements);
//			}
			if (asset.getUser() != null) {
				dao.setUser(asset.getUser().getId());
			}
			if(asset.getAssetCategory() != null)
				dao.setAsset_category(AssetCategoryDAO.create(asset.getAssetCategory()));
		}
		return dao;
	}
	
	public Asset mapToEntity() {
		Asset asset = new Asset();
		
		asset.setId(id);
		asset.setName(name);
		if (type != null) 
			asset.setType(type);
		
		asset.setLabel(label);
		//asset.setDescription(description);
		asset.setLocation(geo_location);
		if (parent != null) 
			asset.setParentAsset(parent.mapToEntity());

		if(asset_category != null)
			asset.setAssetCategory(asset_category.mapToEntity());
		
//		if (owner != null) {
//			User entityOwner = new User();
//			entityOwner.setId(owner);
////			asset.setOwner(entityOwner);
//		}
//		if (user != null) {
//			User entityUser = new User();
//			entityUser.setId(user);
//			asset.setUser(entityUser);
//		}
		return asset;
	}
}
