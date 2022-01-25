package com.renergetic.backdb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "demand")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Demand {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	// FOREIGN KEY FROM ASSETS TABLE
	@Column(name = "asset_id")
	private long assetId;
	
	// FOREIGN KEY FROM INFRASTRUCTURE TABLE
	@Column(name = "output_infrastructure_id")
	private long outputInfrastructureId;
	
	// REFERENCE TO POWER TIMESERIES
	@Column(name = "power")
	private long power;

	public Demand(String name, String description, long asset_id, long output_infrastructure_id, long power) {
		super();
		this.name = name;
		this.description = description;
		this.assetId = asset_id;
		this.outputInfrastructureId = output_infrastructure_id;
		this.power = power;
	}
}
