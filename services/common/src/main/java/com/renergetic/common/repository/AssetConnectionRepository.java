package com.renergetic.common.repository;

import com.renergetic.common.model.AssetConnection;
import com.renergetic.common.model.ConnectionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@SuppressWarnings("unchecked")
public interface AssetConnectionRepository extends JpaRepository<AssetConnection, Long> {
	AssetConnection save(AssetConnection dashboard);
	List<AssetConnection> findByAssetId(Long assetId);
	List<AssetConnection> findByConnectedAssetIdAndConnectionType(Long connectedAssetId, ConnectionType connectionType);
	void deleteByAssetIdAndConnectedAssetIdAndConnectionType(Long assetId, Long connectedAssetId, ConnectionType connectionType);

	boolean existsByAssetIdAndConnectedAssetIdAndConnectionType(Long assetId, Long connectedAssetId, ConnectionType connectionType);
}
