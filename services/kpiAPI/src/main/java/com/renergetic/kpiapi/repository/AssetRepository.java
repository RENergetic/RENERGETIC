package com.renergetic.kpiapi.repository;

import com.renergetic.kpiapi.model.Asset;
import com.renergetic.kpiapi.model.User;
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

    public Optional<Asset> findByName(String name);

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

    List<Asset> findByAssetCategoryId(Long categoryId, Pageable pageable);

    List<Asset> findByAssetCategoryId(Long categoryId);
}
