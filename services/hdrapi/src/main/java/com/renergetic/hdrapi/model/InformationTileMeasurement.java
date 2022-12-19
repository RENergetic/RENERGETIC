package com.renergetic.hdrapi.model;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.AccessLevel;
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
	
	@OneToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_category_id", nullable = true, insertable = true, updatable = true)
	// TODO: ask about if we need this field
	private AssetCategory assetCategory;

	@Column(name = "direction", nullable = true, insertable = true, updatable = true)
	@Enumerated(EnumType.STRING)
	private Direction direction;

	@Column(name = "domain", nullable = true, insertable = true, updatable = true)
	@Enumerated(EnumType.STRING)
	private Domain domain;

	@Column(name = "agreggation_function", nullable = true, insertable = true, updatable = true)
	private String function;

	// FOREIGN KEY FROM MEASUREMENT TABLE
	@Getter(AccessLevel.NONE)
	@ManyToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "measurement_id", nullable = true, insertable = true, updatable = true)
	private Measurement measurement;
	

	@ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "information_tile_id", nullable = false, insertable = true, updatable = true)
    private InformationTile informationTile;

	public Measurement getMeasurement() {
		if(measurement!=null)
 			measurement.setFunction(function);
		return measurement;
	}
}
