package com.renergetic.backdb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "connection")
@RequiredArgsConstructor
@ToString
public class Connection {
	@Getter
	@Setter
	@Id
	@JsonProperty(access = Access.READ_ONLY)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Getter
	@Setter
	@Column(name = "name", nullable = true, insertable = true, updatable = true)
	private String name;

	@Getter
	@Setter
	@Column(name = "description", nullable = true, insertable = true, updatable = true)
	private String description;

	@OneToOne(optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = false, insertable = true, updatable = true)
	private Asset asset;
	
	// FOREIGN KEY, THE INFRASTUCTURE TO INPUT THE ENERGY
	@OneToOne(optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "input_infrastructure_id", nullable = false, insertable = true, updatable = true)
	private Infrastructure inputInfrastructure;
	
	// FOREIGN KEY, THE INFRASTUCTURE TO GET THE ENERGY
	@OneToOne(optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "output_infrastructure_id", nullable = false, insertable = true, updatable = true)
	private Infrastructure outputInfrastructure;
	
	// ID OF INPUT POWER FROM TIMESERIES IN INFLUX DB
	@Getter
	@Setter
	@Column(name = "input_power")
	private long inputPower;
	
	// ID OF OUTPUTPOWER FROM TIMESERIES IN INFLUX DB
	@Getter
	@Setter
	@Column(name = "output_power")
	private long outputPower;

	public Connection(String name, String description, long asset_id, long input_infrastructure_id, long output_infrastructure_id, long input_power, long output_power) {
		super();
		this.name = name;
		this.description = description;
		this.asset = new Asset();
		this.asset.setId(asset_id);
		this.inputInfrastructure = new Infrastructure();
		this.inputInfrastructure.setId(input_infrastructure_id);
		this.outputInfrastructure = new Infrastructure();
		this.outputInfrastructure.setId(output_infrastructure_id);
		this.inputPower = input_power;
		this.outputPower = output_power;
	}

	public void setInputInfrastructure(Long id) {
		this.inputInfrastructure = new Infrastructure();
		this.inputInfrastructure.setId(id);
	}

	public void setOutputInfrastructure(Long id) {
		this.outputInfrastructure = new Infrastructure();
		this.outputInfrastructure.setId(id);
	}

	public Infrastructure getInputInfrastructure() {
		return this.inputInfrastructure;
	}

	public Infrastructure getOutputInfrastructure() {
		return this.outputInfrastructure;
	}

	public void setAsset(Long id) {
		this.asset = new Asset();
		this.asset.setId(id);
	}

	public Asset getAsset() {
		return this.asset;
	}
}
