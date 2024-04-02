package com.renergetic.kpiapi.model;

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
@Table(name = "abstract_meter")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class AbstractMeterConfig {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "name", nullable = false, insertable = true, updatable = true, unique = false)
	private AbstractMeter name;

	@Column(name = "formula", nullable = false, insertable = true, updatable = true, unique = false)
	private String formula;

	@Column(name = "condition", nullable = true, insertable = true, updatable = true, unique = false)
	private String condition;

	@Enumerated(EnumType.STRING)
	@Column(name = "domain", nullable = false, insertable = true, updatable = true, unique = false)
	private Domain domain;
}
