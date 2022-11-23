package com.renergetic.ingestionapi.model;

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
import javax.persistence.OneToOne;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Subselect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Subselect("select * from measurement")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Measurement {

	//todo: unique key: name-asset-sensor-type-direction
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", nullable = false, insertable = true, updatable = true)
	private String name;

	@Column(name = "sensor_name", nullable = false, insertable = true, updatable = true)
	private String sensorName;

	@Column(name = "label", nullable = true, insertable = true, updatable = true)
	private String label;

	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = true, insertable = true, updatable = true)
	private Asset asset;

	@OneToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_category_id", nullable = true, insertable = true, updatable = true)
	private AssetCategory assetCategory;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "measurement_type_id", nullable = false, insertable = true, updatable = true)
	private MeasurementType type;

	@Column(name = "direction", nullable = true, insertable = true, updatable = true)
	@Enumerated(EnumType.STRING)
	private Direction direction;

	@Column(name = "domain", nullable = true, insertable = true, updatable = true)
	@Enumerated(EnumType.STRING)
	private Domain domain;

	@Column(name = "sensor_id", nullable = true) //todo unique key sensor_id and measurement type
	private String sensorId;

	// FOREIGN KEY FROM ASSETS TABLE
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "energy_island_asset_id", nullable = true, insertable = true, updatable = true)
	private Asset island;
	
	public Measurement(Long id, String uuid, String name, String label,Asset asset, Direction direction) {
		super();
		this.id = id;
		this.name = name;
		this.label = label;
		this.asset = asset;
		this.direction = direction;
		this.type = new MeasurementType();
	}
}
