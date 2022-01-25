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
@Table(name = "infrastructure")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Infrastructure {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "type")
	private String type;
	
	// FOREIGN KEY FROM USERS TABLE
	@Column(name = "operator_user_id")
	private long operatorUserId;
	
	// ID OF ENERGY STORED FROM TIMESERIES IN INFLUX DB
	@Column(name = "energy_stored")
	private long energyStored;

	public Infrastructure(String name, String description, String type, long operator_user_id, long energy_stored) {
		super();
		this.name = name;
		this.description = description;
		this.type = type;
		this.operatorUserId = operator_user_id;
		this.energyStored = energy_stored;
	}
}
