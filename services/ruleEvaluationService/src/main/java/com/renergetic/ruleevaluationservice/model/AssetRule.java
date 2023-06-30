package com.renergetic.ruleevaluationservice.model;

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
    /*
        TODO: Validator:
            measurement1 completely filled
                and
                measurement2 completely filled
                    or
                compareToConfigThreshold false and threshold filled
                    or
                compareToConfigThreshold true and asset has threshold configured
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "measurement_1", nullable = false)
    private Measurement measurement1;

    @Column(name = "measurement_1_function", nullable = false)
    private String functionMeasurement1;

    @Column(name = "measurement_1_time_range", nullable = false)
    private String timeRangeMeasurement1;

    @Column(name = "measurement_2", nullable = true)
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
}