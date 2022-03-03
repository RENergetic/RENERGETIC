package com.renergetic.backdb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "measurement_type")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class MeasurementType {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "name", nullable = false, insertable = true, updatable = true)
	private String name;
	
	@Column(name = "label", nullable = true, insertable = true, updatable = true)
	private String label;
	
	@Column(name = "unit", nullable = true, insertable = true, updatable = true)
	@Enumerated(EnumType.STRING)
	private Unit unit;

	public MeasurementType(long id, String name, String label, Unit unit) {
		super();
		this.id = id;
		this.name = name;
		this.label = label;
		this.unit = unit;
	}
}
