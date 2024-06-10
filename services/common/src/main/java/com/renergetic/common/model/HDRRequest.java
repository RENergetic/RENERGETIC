package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "hdr_request", uniqueConstraints =
@UniqueConstraint(columnNames = {"timestamp"}))
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class HDRRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Long timestamp;

    @Column(name = "date_from", nullable = false)
    private Long dateFrom;
    @Column(name = "date_to", nullable = false)
    private Long dateTo;
    @Column(name = "max_value")
    private Double maxValue;
    @Column(name = "value_change")
    private Double valueChange;
    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "measurement_type_id", nullable = true, insertable = true, updatable = true)
    private MeasurementType valueType;
    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "asset_id", nullable = true, insertable = true, updatable = true)
    private Asset asset;
    @Column(name = "json_config", columnDefinition = "TEXT")
    private String jsonConfig;
}
