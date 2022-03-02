package com.renergetic.backdb.repository.information;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.details.AssetDetails;

@SuppressWarnings("unchecked")
public interface AssetDetailsRepository extends JpaRepository<AssetDetails, Long> {
	AssetDetails save(AssetDetails information);

	List<AssetDetails> findByAssetId(long resource_id);
}
