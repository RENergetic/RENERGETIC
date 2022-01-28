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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "supply")
@RequiredArgsConstructor
@ToString
public class Supply {
	@Id
	@Getter
	@Setter
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
	
	// FOREIGN KEY FROM ASSETS TABLE
	@OneToOne(optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = false, insertable = true, updatable = true)
	private Asset asset;
	
	// FOREIGN KEY FROM INFRASTRUCTURE TABLE
	@OneToOne(optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "infrastructure_id", nullable = false, insertable = true, updatable = true)
	private Infrastructure infrastructure;
	
	// REFERENCE TO POWER TIMESERIES
	@Getter
	@Setter
	@Column(name = "power")
	private long power;

	public Supply(String name, String description, long asset_id, long input_infrastructure_id, long power) {
		super();
		this.name = name;
		this.description = description;
		this.asset = new Asset();
		this.asset.setId(asset_id);
		this.infrastructure = new Infrastructure();
		this.infrastructure.setId(input_infrastructure_id);
		this.power = power;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Long id) {
		this.asset = new Asset();
		this.asset.setId(id);
	}

	public Infrastructure getInfrastructure() {
		return infrastructure;
	}

	public void setInfrastructure(Long id) {
		this.infrastructure = new Infrastructure();
		this.infrastructure.setId(id);
	}
	
}
