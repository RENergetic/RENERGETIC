package com.renergetic.hdrapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.User;

@SuppressWarnings("unchecked")
public interface AssetRepository extends JpaRepository<Asset, Long> {
	public Asset save(Asset asset);
	
	public List<Asset> findByParentAsset(Asset parentAsset);
	public Optional<Asset> findByName(String name);

	@Query(value = "SELECT asset_conn.* " +
			"FROM (asset asset_conn " +
			"INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
			"INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId)" +
			"LIMIT :limit OFFSET :offset ;", nativeQuery = true)
	public List<Asset> findByUserId(Long userId, long offset, int limit);

	@Query(value = "SELECT asset_conn.* " +
			"FROM (asset asset_conn " +
			"INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
			"INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId)" +
			"WHERE asset_conn.asset_category_id = :category " +
			"LIMIT :limit OFFSET :offset ;", nativeQuery = true)
	public List<Asset> findByUserIdAndCategoryId(Long userId, Long category, long offset, int limit);

	List<Asset> findByUser(User userId);

	List<Asset> findByAssetCategoryId(Long categoryId, Pageable pageable);
	List<Asset> findByAssetCategoryId(Long categoryId);
}
