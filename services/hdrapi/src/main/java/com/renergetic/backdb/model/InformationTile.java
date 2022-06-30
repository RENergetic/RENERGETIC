package com.renergetic.backdb.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

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

    @Column(name = "name", nullable = false)
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
    @JoinColumn(name = "information_panel_id")
    private InformationPanel informationPanel;

}
