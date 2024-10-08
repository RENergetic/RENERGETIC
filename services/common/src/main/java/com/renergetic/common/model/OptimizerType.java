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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="name", unique = true, nullable = false)
    private String name;

    @Column(name="domains_quantity", unique = false, nullable = true)
    private Integer domainsQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type_a", nullable = true, insertable = true, updatable = true)
    private ConnectionType connectionTypeA;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type_b", nullable = true, insertable = true, updatable = true)
    private ConnectionType connectionTypeB;

    @Column(name = "connection_type_a_readable", nullable = true, insertable = true, updatable = true)
    private String connectionTypeAReadable;

    @Column(name = "connection_type_b_readable", nullable = true, insertable = true, updatable = true)
    private String connectionTypeBReadable;

    @OneToMany(cascade = {}, fetch = FetchType.EAGER, orphanRemoval = false, mappedBy = "optimizerType")
    private List<OptimizerParameter> optimizerParameters;
}
