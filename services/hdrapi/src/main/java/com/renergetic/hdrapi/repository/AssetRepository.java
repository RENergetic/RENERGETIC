package com.renergetic.hdrapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.User;

@SuppressWarnings("unchecked")
public interface AssetRepository extends JpaRepository<Asset, Long> {
	Asset save(Asset asset);
	
	List<Asset> findByParentAsset(Asset parentAsset);
	List<Asset> findByAssets(Asset asset);

	@Query(value = "SELECT asset_conn.* " +
			"FROM (asset asset_conn " +
			"INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
			"INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId)" +
			"LIMIT :limit OFFSET :offset ;", nativeQuery = true)
	public List<Asset> findByUserId(Long userId, long offset, int limit);



	List<Asset> findByUser(User userId);
}
