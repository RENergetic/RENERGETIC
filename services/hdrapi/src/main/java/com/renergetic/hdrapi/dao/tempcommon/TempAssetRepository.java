package com.renergetic.hdrapi.dao.tempcommon;

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
public interface TempAssetRepository extends JpaRepository<Asset, Long> {


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

}
