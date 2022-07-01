package com.renergetic.backdb.repository;

import com.renergetic.backdb.model.DemandSchedule;
import com.renergetic.backdb.model.InformationPanel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InformationPanelRepository extends JpaRepository<InformationPanel, Long> {

    @Query(value="SELECT * from information_panel WHERE information_panel.owner_id = :ownerId", nativeQuery = true)
    public List<InformationPanel> findAllByOwnerId(Long ownerId);
    public Optional<InformationPanel> findByName(String name);

    @Query(value = "SELECT information_panel.* " +
            "FROM (asset asset_conn " +
            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
            "INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId " +
            "INNER JOIN asset_panel ON asset_panel.asset_id = asset_conn.id " +
            "INNER JOIN information_panel ON information_panel.id = asset_panel.panel_id) " +
            "LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<InformationPanel> findByUserId(Long userId, long offset, int limit);
}
