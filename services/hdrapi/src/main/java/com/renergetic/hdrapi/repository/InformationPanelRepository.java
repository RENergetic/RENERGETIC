package com.renergetic.hdrapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.renergetic.hdrapi.model.InformationPanel;

import java.util.List;
import java.util.Optional;

public interface InformationPanelRepository extends JpaRepository<InformationPanel, Long> {

    @Query(value="SELECT * from information_panel WHERE information_panel.owner_id = :ownerId", nativeQuery = true)
    public List<InformationPanel> findAllByOwnerId(Long ownerId);
    public Optional<InformationPanel> findByName(String name);

    @Query(value = "SELECT DISTINCT information_panel.* " +
            "FROM (information_panel " +
            "INNER JOIN asset_panel ON  information_panel.id = asset_panel.panel_id " +
            "INNER JOIN asset asset_conn ON information_panel.id = asset_panel.panel_id " +
            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
            "INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId " +
            ") " +
            "LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<InformationPanel> findByUserId(Long userId, long offset, int limit);
}
