package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "optimizer_type")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class OptimizerType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", unique = true, nullable = false)
    private String name;

    @OneToMany(cascade = {}, fetch = FetchType.EAGER, orphanRemoval = false)
    @JoinColumn(name = "optimizer_parameters_id")
    private List<OptimizerParameter> optimizerParameters;
}
