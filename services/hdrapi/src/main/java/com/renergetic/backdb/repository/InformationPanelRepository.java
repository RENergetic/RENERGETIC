package com.renergetic.backdb.repository;

import com.renergetic.backdb.model.InformationPanel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InformationPanelRepository extends JpaRepository<InformationPanel, Long> {

    @Query(value="SELECT * from information_panel WHERE information_panel.owner_id = :ownerId", nativeQuery = true)
    public List<InformationPanel> findAllByOwnerId(Long ownerId);
    public Optional<InformationPanel> findByName(String name);
}
