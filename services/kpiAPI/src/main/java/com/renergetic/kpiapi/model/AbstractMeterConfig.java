package com.renergetic.kpiapi.model;

import javax.persistence.*;

import com.renergetic.common.model.Domain;
import com.renergetic.common.model.Measurement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "abstract_meter")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class AbstractMeterConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, insertable = true, updatable = true, unique = false)
    private AbstractMeter name; //-> change to type

    @Column(name = "formula", nullable = false, insertable = true, updatable = true, unique = false)
    private String formula;

    @Column(name = "custom_name", nullable = true, insertable = true, updatable = true, unique = false)
    private String customName;

    @Column(name = "condition", nullable = true, insertable = true, updatable = true, unique = false)
    private String condition;

    @Enumerated(EnumType.STRING)
    @Column(name = "domain", nullable = false, insertable = true, updatable = true, unique = false)
    private Domain domain;

    @ManyToOne(
            optional = true,
            cascade = {CascadeType.REFRESH}
    )
    @NotFound(
            action = NotFoundAction.IGNORE
    )
    @JoinColumn(
            name = "measurement_id",
            nullable = true,
            insertable = true,
            updatable = true
    )
    private Measurement measurement;
}
