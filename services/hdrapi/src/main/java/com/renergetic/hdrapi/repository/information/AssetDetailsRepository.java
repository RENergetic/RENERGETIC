package com.renergetic.hdrapi.repository.information;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.details.AssetDetails;

@SuppressWarnings("unchecked")
public interface AssetDetailsRepository extends JpaRepository<AssetDetails, Long> {
	AssetDetails save(AssetDetails information);

	Boolean existsByIdAndAssetId(long resourceId, long assetId);
	List<AssetDetails> findByAssetId(long resourceId);

	boolean existsByKeyAndAssetId(String key, long resourceId);
	
}
