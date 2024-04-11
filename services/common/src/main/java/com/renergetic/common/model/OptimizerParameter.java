package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "optimizer_parameter")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class OptimizerParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "parameter_name", unique = true, nullable = false)
    private String parameterName;

    @Column(name = "required", unique = false, nullable = false)
    private boolean required;

    @ManyToOne(optional = false)
    @JoinColumn(name = "optimizer_type_id")
    private OptimizerType optimizerType;
}
