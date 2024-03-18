package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_threshold")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class AlertThreshold {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "threshold_type", nullable = false, insertable = true, updatable = true)
	private ThresholdType thresholdType;

	@Column(name = "threshold_constraint", nullable = false, insertable = true, updatable = true)
	private String thresholdConstraint;

	@Column(name = "threshold_update", nullable = false, insertable = true, updatable = true)
	private LocalDateTime thresholdUpdate;

	@Enumerated(EnumType.STRING)
	@Column(name = "aggregation_type", nullable = true, insertable = true, updatable = true)
	private AggregationType aggregationType;

	@Column(name = "aggregation_interval", nullable = true, insertable = true, updatable = true)
	private Long aggregationInterval;

	// FOREIGN KEY FROM MEASUREMENT TABLE
	@ManyToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "measurement_id", nullable = false, insertable = true, updatable = true)
	private Measurement measurement;
}
