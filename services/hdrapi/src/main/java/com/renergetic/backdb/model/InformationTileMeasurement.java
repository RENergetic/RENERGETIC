package com.renergetic.backdb.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "information_tile_measurement")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class InformationTileMeasurement {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "props", nullable = true, insertable = true, updatable = true)
	private String props;

	@Column(name = "measurement_name", nullable = true, insertable = true, updatable = true)
	private String measurementName;

	@Column(name = "sensor_name", nullable = true, insertable = true, updatable = true)
	private String sensorName;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "measurement_type_id", nullable = true, insertable = true, updatable = true)
	private MeasurementType type;

	@Column(name = "direction", nullable = true, insertable = true, updatable = true)
	@Enumerated(EnumType.STRING)
	private Direction direction;

	@Column(name = "domain", nullable = true, insertable = true, updatable = true)
	@Enumerated(EnumType.STRING)
	private Domain domain;
	

	// FOREIGN KEY FROM MEASUREMENT TABLE
	@ManyToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "measurement_id", nullable = true, insertable = true, updatable = true)
	private Measurement measurement;
	

	@ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "information_tile_id", nullable = false, insertable = true, updatable = true)
    private InformationTile informationTile;

}
