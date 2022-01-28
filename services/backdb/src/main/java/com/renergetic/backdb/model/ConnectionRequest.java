package com.renergetic.backdb.model;

import java.io.Serializable;

import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class ConnectionRequest implements Serializable{

	private static final long serialVersionUID = -500130724112844066L;

	@Getter
	@Setter
	@JsonProperty(access = Access.READ_ONLY)
	private Long id;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String description;

	private Long asset;
	
	// FOREIGN KEY, THE INFRASTUCTURE TO INPUT THE ENERGY
	private Long inputInfrastructure;
	
	// FOREIGN KEY, THE INFRASTUCTURE TO GET THE ENERGY
	private Long outputInfrastructure;
	
	// ID OF INPUT POWER FROM TIMESERIES IN INFLUX DB
	@Getter
	@Setter
	private Long inputPower;
	
	// ID OF OUTPUTPOWER FROM TIMESERIES IN INFLUX DB
	@Getter
	@Setter
	private Long outputPower;

	public ConnectionRequest(String name, String description, long asset_id, long input_infrastructure_id, long output_infrastructure_id, long input_power, long output_power) {
		super();
		this.name = name;
		this.description = description;
		this.asset = asset_id;
		this.inputInfrastructure = input_infrastructure_id;
		this.outputInfrastructure = output_infrastructure_id;
		this.inputPower = input_power;
		this.outputPower = output_power;
	}

	public void setInputInfrastructure(Long id) {
		this.inputInfrastructure = id;
	}

	public void setOutputInfrastructure(Long id) {
		this.outputInfrastructure = id;
	}

	public Long getInputInfrastructure() {
		return this.inputInfrastructure;
	}

	public Long getOutputInfrastructure() {
		return this.outputInfrastructure;
	}

	public void setAsset(Long id) {
		this.asset = id;
	}

	public Long getAsset() {
		return this.asset;
	}
	
	public Connection mapToEntity() {
		return new ModelMapper().map(this, Connection.class);
	}
}
