package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "rule_definition")
@RequiredArgsConstructor
@Getter
@Setter
public class RuleDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "measurement_1", nullable = false, insertable = true, updatable = true)
    private RuleDefinitionMeasurement measurement1;

    @OneToOne(optional = true)
    @JoinColumn(name = "measurement_2", nullable = true, insertable = true, updatable = true)
    private RuleDefinitionMeasurement measurement2;

    @Column(name = "compare_manual_threshold", nullable = true)
    private String manualThreshold;

    @Column(name = "comparator", nullable = false)
    private String comparator;
}
