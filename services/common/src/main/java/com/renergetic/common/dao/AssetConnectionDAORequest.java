package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.AssetConnection;
import com.renergetic.common.model.ConnectionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class AssetConnectionDAORequest {
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;
	
	@JsonProperty(value = "asset_id", required = true)
	private Long assetId;

	@JsonProperty(value = "asset_connected_id", required = true)
	private Long assetConnectedId;

	@JsonProperty(value = "type", required = false)
	private ConnectionType type;
	
	public static AssetConnectionDAORequest create(AssetConnection connection) {
		AssetConnectionDAORequest dao = null;
		
		if (connection != null) {
			dao = new AssetConnectionDAORequest();

			//dao.setId(connection.getId());
			
			if (connection.getAsset() != null)
				dao.setAssetId(connection.getAsset().getId());
			if (connection.getConnectedAsset() != null)
				dao.setAssetConnectedId(connection.getConnectedAsset().getId());
			
			dao.setType(connection.getConnectionType());
		}
		return dao;
	}
	
	public AssetConnection mapToEntity() {
		AssetConnection connection = new AssetConnection();
		
		connection.setId(id);
		connection.setConnectionType(type);
		if (assetId != null) {
			Asset asset = new Asset();
			asset.setId(assetId);
			connection.setAsset(asset);
		}
		if (assetConnectedId != null) {
			Asset asset = new Asset();
			asset.setId(assetConnectedId);
			connection.setConnectedAsset(asset);
		}
		return connection;
	}
}
