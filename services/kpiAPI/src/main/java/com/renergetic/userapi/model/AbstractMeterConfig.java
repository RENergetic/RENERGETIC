package com.renergetic.userapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
	@Column(name = "name", nullable = false, insertable = true, updatable = true, unique = true)
	private AbstractMeter name;

	@Column(name = "formula", nullable = false, insertable = true, updatable = true, unique = false)
	private String formula;

	@Column(name = "condition", nullable = true, insertable = true, updatable = true, unique = false)
	private String condition;

	@Column(name = "domain", nullable = false, insertable = true, updatable = true, unique = false)
	private Domain domain;
}
