package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "rule_definition_measurement")
@RequiredArgsConstructor
@Getter
@Setter
public class RuleDefinitionMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "function", nullable = false)
    private String function;

    @Column(name = "multiplier", nullable = true)
    private Float multiplier;

    @ManyToOne(optional = false)
    @JoinColumn(name = "measurement", nullable = false, insertable = true, updatable = true)
    private Measurement measurement;

    @Column(name = "range_from", nullable = false)
    private String rangeFrom;

    @Column(name = "range_to", nullable = false)
    private String rangeTo;
}
