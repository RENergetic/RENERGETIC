package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.List;

@Entity
@Table(name = "information_tile")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class InformationTile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
