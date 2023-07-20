package com.renergetic.common.repository.information;

import com.renergetic.common.model.details.AssetDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface AssetDetailsRepository extends JpaRepository<AssetDetails, Long> {
	AssetDetails save(AssetDetails information);

	Boolean existsByIdAndAssetId(long resourceId, long assetId);
	List<AssetDetails> findByAssetId(long resourceId);

	boolean existsByKeyAndAssetId(String key, long resourceId);

	Optional<AssetDetails> findByKeyAndAssetId(String key, Long assetId);

	void deleteByKeyAndAssetId(String key, Long assetId);
	
}
