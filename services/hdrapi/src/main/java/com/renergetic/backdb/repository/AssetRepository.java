package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Measurement;
import com.renergetic.backdb.model.User;

@SuppressWarnings("unchecked")
public interface AssetRepository extends JpaRepository<Asset, Long> {
	Asset save(Asset asset);
	
	List<Asset> findByParentAsset(Asset parentAsset);
	List<Asset> findByAssets(Asset asset);
	List<Asset> findByUser(User userId);
}
