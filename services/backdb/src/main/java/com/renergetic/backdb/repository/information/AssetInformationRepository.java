package com.renergetic.backdb.repository.information;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.information.AssetInformation;

@SuppressWarnings("unchecked")
public interface AssetInformationRepository extends JpaRepository<AssetInformation, Long> {
	AssetInformation save(AssetInformation information);

	List<AssetInformation> findByName (String name);
	List<AssetInformation> findByAssetId(long resource_id);
	AssetInformation findByIdAndAssetId(long id, long resource_id);
}
