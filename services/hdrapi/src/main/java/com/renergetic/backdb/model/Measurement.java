package com.renergetic.backdb.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@ToString
public class Measurement {
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Getter
	@Setter
	@Column(name = "uuid", nullable = true, insertable = true, updatable = true, unique = true)
	private String uuid;

	@Getter
	@Setter
	@Column(name = "name", nullable = false, insertable = true, updatable = true)
	private String name;

	@Getter
	@Setter
	@Column(name = "label", nullable = true, insertable = true, updatable = true)
	private String label;

	@Getter
	@Setter
	@Column(name = "description", nullable = true, insertable = true, updatable = true)
	private String description;

	@Getter
	@Setter
	@Column(name = "icon", nullable = true, insertable = true, updatable = true)
	private String icon;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "measurement_type_id", nullable = false, insertable = true, updatable = true)
	private MeasurementType type;

	@Getter
	@Setter
	@Column(name = "direction", nullable = true, insertable = true, updatable = true)
	@Enumerated(EnumType.STRING)
	private Direction direction;
	
	// FOREIGN KEY FROM ASSETS TABLE
	@Getter
	@Setter
	@ManyToMany(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinTable(
			name = "asset_measurement",
			joinColumns = @JoinColumn(name = "measurement_id", nullable = true, insertable = true, updatable = true),
			inverseJoinColumns = @JoinColumn(name = "asset_id"))
	private List<Asset> assets;

	public void setType(Long type_id) {
		if (type_id != null) {
			this.type = new MeasurementType();
			this.type.setId(type_id);
		}
	}
	
	public Measurement(Long id, String uuid, String name, String label, String description, String icon, Direction direction) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.name = name;
		this.label = label;
		this.description = description;
		this.icon = icon;
		this.uuid = uuid;
		this.direction = direction;
		this.type = new MeasurementType();
		this.assets = new ArrayList<>();
	}
}
