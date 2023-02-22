package com.renergetic.hdrapi.repository;

import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    //	@Query("SELECT u FROM User u WHERE u.keycloakId = :keycloakId")
    @Query("SELECT asset FROM Asset asset " +
            " WHERE asset.user.id = :userId and asset.type.name ='user'")
    Asset findByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("SELECT asset FROM Asset asset " +
            " WHERE asset.user.id = :userId and asset.type.name ='user'")
    void deleteUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE  Asset asset SET asset.user.id = NULL " +
            " WHERE asset.user.id = :userId and asset.type.name <> 'user'")
    void clearUserId(@Param("userId") Long userId);


    List<Asset> findByAssetCategoryId(Long categoryId, Pageable pageable);

    List<Asset> findByAssetCategoryId(Long categoryId);
}
