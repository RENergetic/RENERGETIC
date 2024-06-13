package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "rule_action")
@RequiredArgsConstructor
@Getter
@Setter
public class RuleAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "asset", nullable = false, insertable = true, updatable = true)
    private Asset asset;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "demand_definition", nullable = false, insertable = true, updatable = true)
    private DemandDefinition demandDefinition;

    @Column(name = "fixed_duration", nullable = false)
    private String fixedDuration;
}
