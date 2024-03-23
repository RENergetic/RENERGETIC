package com.renergetic.common.repository;

import com.renergetic.common.model.Asset;
import com.renergetic.common.model.AssetType;
import com.renergetic.common.model.ConnectionType;
import com.renergetic.common.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface AssetRepository extends JpaRepository<Asset, Long> {
    public Asset save(Asset asset);

    public List<Asset> findByParentAsset(Asset parentAsset);

    public List<Asset> findByType(AssetType assetType);

    public Optional<Asset> findByName(String name);

    @Query(value = "SELECT asset_conn.* " +
            "FROM (asset asset_conn " +
            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
            "INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId)" +
            "LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<Asset> findByUserId(Long userId, long offset, int limit);

    @Query(value = "SELECT asset_conn.* " +
            " FROM (asset asset_conn " +
            " INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
            " INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId)" +
            " WHERE asset_connection.connection_type in :connTypes " +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<Asset> findByUserIdConnectionTypes(@Param("userId") Long userId,
                                                   @Param("connTypes") List<String> connectionTypes,
                                                   long offset, int limit);


    @Query(value = "SELECT asset_conn.* " +
            "FROM (asset asset_conn " +
            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
            "INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId)" +
            "WHERE asset_conn.asset_category_id = :category " +
            "LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<Asset> findByUserIdAndCategoryId(Long userId, Long category, long offset, int limit);

    List<Asset> findByUser(User userId);

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Asset a set"
            + " a.name = :#{#asset.name}, a.label = :#{#asset.label}, a.location = :#{#asset.location},"
            + " a.parentAsset = :#{#asset.parentAsset}, a.user = :#{#asset.user}, a.assetCategory = :#{#asset.assetCategory},"
            + " a.type = :#{#asset.type} where a.id = :#{#asset.id}")
    public void update(@Param("asset") Asset asset);

    //	@Query("SELECT u FROM User u WHERE u.keycloakId = :keycloakId")
    @Query("SELECT asset FROM Asset asset " +
            " WHERE asset.user.id = :userId and asset.type.name ='user'")
    Asset findByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT m_asset.* " +
            "FROM  asset m_asset " +
            "LEFT JOIN asset_type ON asset_type.id = m_asset.asset_type_id " +
            "LEFT JOIN asset_category   ON asset_category.id = m_asset.asset_category_id " +
            " WHERE COALESCE(asset_category.name ilike CONCAT('%', :categoryName, '%'),:categoryName is null) " +
            " AND COALESCE(m_asset.name ilike CONCAT('%', :name, '%'),:name is null ) " +
            " AND COALESCE(m_asset.label ilike CONCAT('%', :label,'%'),:label is null ) " +
            " AND COALESCE(asset_type.name ilike CONCAT('%', :typeName, '%'),:typeName is null) " +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<Asset> filterAssets(@Param("name") String name, @Param("label") String label,
                                    @Param("categoryName") String categoryName, @Param("typeName") String typeName,
                                    @Param("offset") long offset, @Param("limit") int limit);

    @Query(value = "SELECT m_asset.* " +
            "FROM  asset m_asset " +
            "LEFT JOIN asset_type ON asset_type.id = m_asset.asset_type_id " +
            "LEFT JOIN asset_details ON asset_details.asset_id = m_asset.id " +
            " WHERE COALESCE(m_asset.name ilike CONCAT('%', :name, '%'),:name is null ) " +
            " AND COALESCE(asset_type.name ilike CONCAT('%', :typeName, '%'),:typeName is null) " +
            " AND  asset_details.key =:mKey AND asset_details.value =:mValue  " +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<Asset> filterByDetail(@Param("mKey") String key, @Param("mValue") String value,
                                      @Param("name") String name, @Param("typeName") String typeName,
                                      @Param("offset") long offset, @Param("limit") int limit);
//    @Modifying
//    @Query(value = "DELETE FROM asset " +
//            " WHERE asset.user_id = :userId and asset.asset_type_id = :userTypeId",nativeQuery = true)
//    void deleteUserId(@Param("userId") Long userId,@Param("userTypeId") Long userTypeId);

    @Modifying
    @Query(value = "UPDATE  asset SET  user_id = NULL " +
            " WHERE user_id = :userId and asset_type_id <> :userTypeId", nativeQuery = true)
    void clearUserId(@Param("userId") Long userId, @Param("userTypeId") Long userTypeId);


    List<Asset> findByAssetCategoryId(Long categoryId, Pageable pageable);

    List<Asset> findByAssetCategoryId(Long categoryId);

    List<Asset> findByConnectionsConnectionTypeAndConnectionsConnectedAssetTypeName(ConnectionType connectionType,
                                                                                    String assetTypeName);

    List<Asset> findDistinctByConnectionsConnectionTypeAndTypeName(ConnectionType connectionType, String assetTypeName);
}
