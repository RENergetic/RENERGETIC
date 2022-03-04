package com.renergetic.backdb.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demand_request")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class DemandRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "measurement_id", nullable = true)
    private Measurement measurement;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "measurement_type_id", nullable = true)
    private MeasurementType measurementType;

    @Column
    private String action;

    @Column
    private String value;

    @Column(name = "demand_request_start", nullable = false)
    private LocalDateTime start;

    @Column(name = "demand_request_stop", nullable = false)
    private LocalDateTime stop;

    @Column
    private String ext;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;
}