package com.renergetic.hdrapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonProperty(required = true)
	@Column(name = "id", nullable = false, insertable = true, updatable = true, unique = true)
	private Long id;
	
	@Column(name = "name", nullable = false, insertable = true, updatable = true, unique = true)
	@JsonProperty(required = true)
	private String name;
	
	@Column(name = "label", nullable = true, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private String label;
	
	@Column(name = "base_unit", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "base_unit", required = false)
	private String baseUnit;
	
	@Column(name = "unit", nullable = true, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private String unit;
	
	@Column(name = "description", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "description", required = false)
	private String description;
	
	@Column(name = "factor", nullable = true, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private Double factor;
	
	@Column(name = "color", nullable = true, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private String color;

	public MeasurementType(long id, String name, String label, String baseUnit, String unit, String metricType, Double factor, String color) {
		super();
		this.id = id;
		this.name = name;
		this.label = label;
		this.baseUnit = baseUnit;
		this.unit = unit;
		this.description = metricType;
		this.factor = factor;
		this.color = color;
	}
}
