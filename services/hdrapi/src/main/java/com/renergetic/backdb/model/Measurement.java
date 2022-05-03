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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "measurement")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Measurement {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", nullable = false, insertable = true, updatable = true)
	private String name;

	@Column(name = "sensor_name", nullable = false, insertable = true, updatable = true)
	private String sensorName;

	@Column(name = "label", nullable = true, insertable = true, updatable = true)
	private String label;

	@Column(name = "description", nullable = true, insertable = true, updatable = true)
	private String description;

	@Column(name = "icon", nullable = true, insertable = true, updatable = true)
	private String icon;

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
	
	// FOREIGN KEY FROM ASSETS TABLE
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "energy_island_asset_id", nullable = true, insertable = true, updatable = true)
	private Asset asset;

	@OneToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "uuid", nullable = false, insertable = true, updatable = false)
	private UUID uuid;
	
	public Measurement(Long id, String uuid, String name, String label, String description, String icon, Direction direction) {
		super();
		this.id = id;
		this.name = name;
		this.label = label;
		this.description = description;
		this.icon = icon;
		this.direction = direction;
		this.type = new MeasurementType();
	}
}
