package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "rule")
@RequiredArgsConstructor
@Getter
@Setter
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "rule_definition", nullable = true, insertable = true, updatable = true)
    private RuleDefinition ruleDefinition;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "rule_action", nullable = true, insertable = true, updatable = true)
    private RuleAction ruleAction;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "positive_rule", nullable = true, insertable = true, updatable = true)
    private Rule positiveRule;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "negative_rule", nullable = true, insertable = true, updatable = true)
    private Rule negativeRule;

    @Column(name = "active", nullable = true)
    private Boolean active;

    @Column(name = "root", nullable = false)
    private Boolean root;
}
