package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Connection;

@SuppressWarnings("unchecked")
public interface AssetRepository extends JpaRepository<Asset, Long> {
	List<Asset> findByName (String name);
	
	Asset save(Asset asset);

	@Transactional
	@Modifying
	@Query("update Asset a set a.name = :#{#asset.name}, a.description = :#{#asset.description}, a.location = :#{#asset.location}, a.partOfAsset.id = :#{#asset.partOfAsset.id}, a.ownerUser.id = :#{#asset.ownerUser.id} where a.id = :id")
	int update(@Param("asset") Asset asset, Long id);

	List<Asset> findByLocation(String location);
	
	@Query("select c from Connection c where c.asset.id = (select a.id from Asset a where a.id = :id)")
	List<Connection> findConnections(Long id);
}
