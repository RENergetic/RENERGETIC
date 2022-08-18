package com.renergetic.hdrapi.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.renergetic.hdrapi.model.InformationTile;

import java.util.List;
import java.util.Optional;

public interface InformationTileRepository extends JpaRepository<InformationTile, Long> {

    @Query(value="SELECT * from information_tile WHERE information_tile.information_panel_id = :informationPanelId", nativeQuery = true)
    public List<InformationTile> findAllByInformationPanelId(Pageable pageable, Long informationPanelId);
    public Optional<InformationTile> findByName(String name);
}