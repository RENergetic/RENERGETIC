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
@Table(name = "connection")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Connection {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "asset_id")
	private long assetId;
	
	// FOREIGN KEY, THE INFRASTUCTURE TO INPUT THE ENERGY
	@Column(name = "input_infrastructure_id")
	private long inputInfrastructureId;
	
	// FOREIGN KEY, THE INFRASTUCTURE TO GET THE ENERGY
	@Column(name = "output_infrastructure_id")
	private long outputInfrastructureId;
	
	// ID OF INPUT POWER FROM TIMESERIES IN INFLUX DB
	@Column(name = "input_power")
	private long inputPower;
	
	// ID OF OUTPUTPOWER FROM TIMESERIES IN INFLUX DB
	@Column(name = "output_power")
	private long outputPower;

	public Connection(String name, String description, long asset_id, long input_infrastructure_id, long output_infrastructure_id, long input_power, long output_power) {
		super();
		this.name = name;
		this.description = description;
		this.assetId = asset_id;
		this.inputInfrastructureId = input_infrastructure_id;
		this.outputInfrastructureId = output_infrastructure_id;
		this.inputPower = input_power;
		this.outputPower = output_power;
	}
}
