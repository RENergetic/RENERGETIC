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
import jakarta.persistence.Table;

@Entity
@Table(name = "user_demand_definition")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class DemandDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_type", nullable = false, insertable = true, updatable = true)
    @Enumerated(EnumType.STRING)
    private DemandDefinitionActionType actionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DemandDefinitionAction action;

    @Column(length = 255)
    private String message;

    @Column
    private String ext;

    @ManyToOne(optional = true, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "information_tile_id", nullable = true, insertable = true, updatable = true)
    private InformationTile informationTile;
}
