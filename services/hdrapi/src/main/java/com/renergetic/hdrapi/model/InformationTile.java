package com.renergetic.hdrapi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
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

    //TODO: this field can be considered to be removed
    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "label")
    private String label;

    @Column(name = "layout")
    private String layout;

    @Column(name = "props")
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
