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
@Table(name = "supply")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Supply {
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
	@Column(name = "input_infrastructure_id")
	private long inputInfrastructureId;
	
	// REFERENCE TO POWER TIMESERIES
	@Column(name = "power")
	private long power;

	public Supply(String name, String description, long asset_id, long input_infrastructure_id, long power) {
		super();
		this.name = name;
		this.description = description;
		this.assetId = asset_id;
		this.inputInfrastructureId = input_infrastructure_id;
		this.power = power;
	}
}
