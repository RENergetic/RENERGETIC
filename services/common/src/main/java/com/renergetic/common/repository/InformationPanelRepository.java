package com.renergetic.common.repository;

import com.renergetic.common.model.Asset;
import com.renergetic.common.model.InformationPanel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface InformationPanelRepository extends JpaRepository<InformationPanel, Long> {

    @Query(value = "SELECT * from information_panel WHERE information_panel.owner_id = :ownerId", nativeQuery = true)
    public List<InformationPanel> findAllByOwnerId(Long ownerId);

    public Optional<InformationPanel> findByName(String name);

    @Query(value = "SELECT DISTINCT information_panel.* " +
            " FROM (information_panel " +
            " LEFT JOIN asset_panel ON  information_panel.id = asset_panel.panel_id and not information_panel.featured" +
            " LEFT JOIN asset asset_conn ON asset_conn.id = asset_panel.asset_id" +
            " LEFT JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id" +
            " LEFT JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId" +
            " ) " +
            " WHERE asset_user.user_id = :userId or featured" +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<InformationPanel> findByUserId(Long userId, long offset, int limit);

    @Query(value = "SELECT DISTINCT information_panel.* " +
            " FROM (information_panel " +
            " LEFT JOIN asset_panel ON  information_panel.id = asset_panel.panel_id and not information_panel.featured" +
            " LEFT JOIN asset asset_conn ON asset_conn.id = asset_panel.asset_id" +
            " LEFT JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id" +
            " ) " +
            " WHERE is_template =:isTemplate and featured" +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<InformationPanel> findFeatured(@Param("isTemplate") boolean isTemplate, long offset, int limit);
    @Modifying
    @Transactional
    @Query(value = " INSERT INTO asset_panel ( panel_id, asset_id) " +
            " SELECT :id, :assetId WHERE NOT EXISTS " +
            " (SELECT * from asset_panel j WHERE j.panel_id = :id and j.asset_id = :assetId)", nativeQuery = true)
    public int assignAsset(@Param("id") Long id, @Param("assetId") Long assetId);

    @Modifying
    @Transactional
    @Query(value = " DELETE FROM asset_panel  WHERE asset_panel.panel_id = :id and asset_panel.asset_id = :assetId ", nativeQuery = true)
    public void revokeAsset(@Param("id") Long id, @Param("assetId") Long assetId);

    @Query("SELECT asset FROM Asset asset JOIN asset.informationPanels informationPanel" +
            "  WHERE informationPanel.id = :panelId ")
    List<Asset> findConnectedAssets(@Param("panelId") Long panelId);


}
