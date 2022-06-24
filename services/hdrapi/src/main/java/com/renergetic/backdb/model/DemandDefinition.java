package com.renergetic.backdb.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "user_demand_definition")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class DemandDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "action_type", nullable = false, insertable = true, updatable = true)
    private String actionType;

    @Column(nullable = false)
    private String action;

    @Column(length = 255)
    private String message;

    @Column
    private String ext;

    @ManyToOne(optional = true, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "information_tile_id", nullable = true, insertable = true, updatable = true)
    private InformationTile informationTile;
}
