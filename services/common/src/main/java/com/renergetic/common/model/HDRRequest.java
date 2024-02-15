package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "date_from", nullable = false)
    private LocalDateTime dateFrom;
    @Column(name = "date_to", nullable = false)
    private LocalDateTime dateTo;
    @Column(name = "max_value")
    private Double maxValue;
    @Column(name = "value_change")
    private Double valueChange;
    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "measurement_type_id", nullable = true, insertable = true, updatable = true)
    private MeasurementType valueType;
    @Column(name = "json_config", columnDefinition = "TEXT")
    private String jsonConfig;
}
