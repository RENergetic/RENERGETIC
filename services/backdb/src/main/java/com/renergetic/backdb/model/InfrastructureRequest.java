package com.renergetic.backdb.model;

import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class InfrastructureRequest {
	@Getter
	@Setter
	@JsonProperty(access = Access.READ_ONLY)
	private long id;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String description;

	@Getter
	@Setter
	private String type;
	
	// FOREIGN KEY FROM USERS TABLE
	private Long operatorUser;
	
	// ID OF ENERGY STORED FROM TIMESERIES IN INFLUX DB
	@Getter
	@Setter
	private Long energyStored;

	public InfrastructureRequest(String name, String description, String type, long operator_user_id, long energy_stored) {
		super();
		this.name = name;
		this.description = description;
		this.type = type;
		this.operatorUser = operator_user_id;
		this.energyStored = energy_stored;
	}

	public Long getOperatorUser() {
		return operatorUser;
	}

	public void setOperatorUser(Long operatorUserId) {
		this.operatorUser = operatorUserId;
	}
	
	public Infrastructure mapToEntity() {
		return new ModelMapper().map(this, Infrastructure.class);
	}
	
}
