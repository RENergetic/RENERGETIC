package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "information_tile")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class InformationTile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "label")
    private String label;

    @Column(name = "layout",columnDefinition="TEXT")
    private String layout;

    @Column(name = "props",columnDefinition="TEXT")
    private String props;
    
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private InformationTileType type;

    @ManyToOne
    @JoinColumn(name = "information_panel_id",nullable = true)
    private InformationPanel informationPanel;

    @OneToMany(cascade = CascadeType.REFRESH, mappedBy = "informationTile")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<InformationTileMeasurement> informationTileMeasurements;
}
