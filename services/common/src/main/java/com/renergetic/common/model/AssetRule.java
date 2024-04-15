package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "asset_rule")
@RequiredArgsConstructor
@Getter
@Setter
public class AssetRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "asset_id", nullable = false, insertable = true, updatable = true)
    private Asset asset;

    @ManyToOne(optional = false)
    @JoinColumn(name = "measurement_1", nullable = false, insertable = true, updatable = true)
    private Measurement measurement1;

    @Column(name = "measurement_1_function", nullable = false)
    private String functionMeasurement1;

    @Column(name = "measurement_1_time_range", nullable = false)
    private String timeRangeMeasurement1;

    @ManyToOne(optional = true)
    @JoinColumn(name = "measurement_2", nullable = true, insertable = true, updatable = true)
    private Measurement measurement2;

    @Column(name = "measurement_2_function", nullable = true)
    private String functionMeasurement2;

    @Column(name = "measurement_2_time_range", nullable = true)
    private String timeRangeMeasurement2;

    @Column(name = "compare_to_config_threshold", nullable = false)
    private boolean compareToConfigThreshold;

    @Column(name = "compare_manual_threshold", nullable = true)
    private String manualThreshold;

    @Column(name = "comparator", nullable = false)
    private String comparator;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "demand_on_true", nullable = false, columnDefinition = "boolean default false")
    private Boolean sendDemandTrue;

    @ManyToOne(optional = true)
    @JoinColumn(name = "demand_on_true_asset_id", nullable = true, insertable = true, updatable = true)
    private Asset demandAssetTrue;

    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "demand_on_true_definition_id", nullable = true, insertable = true, updatable = true)
    private DemandDefinition demandDefinitionTrue;

    @Column(name = "demand_on_false", nullable = false, columnDefinition = "boolean default false")
    private Boolean sendDemandFalse;

    @ManyToOne(optional = true)
    @JoinColumn(name = "demand_on_false_asset_id", nullable = true, insertable = true, updatable = true)
    private Asset demandAssetFalse;

    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "demand_on_false_definition_id", nullable = true, insertable = true, updatable = true)
    private DemandDefinition demandDefinitionFalse;
}
