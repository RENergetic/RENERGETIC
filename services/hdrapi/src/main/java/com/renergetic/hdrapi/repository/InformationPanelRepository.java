package com.renergetic.hdrapi.repository;

import com.renergetic.hdrapi.model.InformationPanel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InformationPanelRepository extends JpaRepository<InformationPanel, Long> {

    @Query(value = "SELECT * from information_panel WHERE information_panel.owner_id = :ownerId", nativeQuery = true)
    public List<InformationPanel> findAllByOwnerId(Long ownerId);

    public Optional<InformationPanel> findByName(String name);

    @Query(value = "SELECT DISTINCT information_panel.* " +
            "FROM (information_panel " +
            "LEFT JOIN asset_panel ON  information_panel.id = asset_panel.panel_id and not information_panel.featured" +
            "LEFT JOIN asset asset_conn ON asset_conn.id = asset_panel.asset_id" +
            "LEFT JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id" +
            "LEFT JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId" +
            "WHERE asset_user.user_id = :userId or featured" +
            ") " +
            "LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<InformationPanel> findByUserId(Long userId, long offset, int limit);



}
