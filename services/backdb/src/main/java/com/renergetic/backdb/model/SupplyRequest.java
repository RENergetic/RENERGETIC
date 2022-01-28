package com.renergetic.backdb.model;

import org.modelmapper.ModelMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class SupplyRequest {
	@Getter
	@Setter
	private Long id;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String description;
	
	// FOREIGN KEY FROM ASSETS TABLE
	private Long asset;
	
	// FOREIGN KEY FROM INFRASTRUCTURE TABLE
	private Long infrastructure;
	
	// REFERENCE TO POWER TIMESERIES
	@Getter
	@Setter
	private Long power;

	public SupplyRequest(String name, String description, long asset_id, long input_infrastructure_id, long power) {
		super();
		this.name = name;
		this.description = description;
		this.asset = asset_id;
		this.infrastructure = input_infrastructure_id;
		this.power = power;
	}

	public Long getAsset() {
		return asset;
	}

	public void setAsset(Long id) {
		this.asset = id;
	}

	public Long getInfrastructure() {
		return infrastructure;
	}

	public void setInfrastructure(Long id) {
		this.infrastructure = id;
	}
	
	public Supply mapToEntity() {
		return new ModelMapper().map(this, Supply.class);
	}
	
}
