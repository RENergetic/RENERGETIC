package com.renergetic.backdb.model;

import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "infrastructure")
@RequiredArgsConstructor
@ToString
public class Infrastructure {
	@Getter
	@Setter
	@JsonIgnore
	public static List<String> ALLOWED_TYPES;
	
	@Id
	@Getter
	@Setter
	@JsonProperty(access = Access.READ_ONLY)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Getter
	@Setter
	@Column(name = "name", nullable = true, insertable = true, updatable = true)
	private String name;

	@Getter
	@Setter
	@Column(name = "description", nullable = true, insertable = true, updatable = true)
	private String description;

	@Getter
	@Setter
	@Column(name = "type", nullable = false, insertable = true, updatable = true)
	private String type;
	
	// FOREIGN KEY FROM USERS TABLE
	@OneToOne(optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "operator_user_id", nullable = true, insertable = true, updatable = true)
	private User operatorUser;
	
	// ID OF ENERGY STORED FROM TIMESERIES IN INFLUX DB
	@Getter
	@Setter
	@Column(name = "energy_stored", nullable = true, insertable = true, updatable = true)
	private Long energyStored;

	public Infrastructure(String name, String description, String type, long operator_user_id, long energy_stored) {
		super();
		this.name = name;
		this.description = description;
		this.type = type;
		this.operatorUser = new User();
		this.operatorUser.setId(operator_user_id);
		this.energyStored = energy_stored;
	}
	
	static {
		ALLOWED_TYPES =  new ArrayList<>();
		
		ALLOWED_TYPES.add("Steam");
		ALLOWED_TYPES.add("Distric Heating");
		ALLOWED_TYPES.add("Cooling");
		ALLOWED_TYPES.add("Electricity");
	}

	public User getOperatorUser() {
		return operatorUser;
	}

	public void setOperatorUser(Long operatorUserId) {
		this.operatorUser = new User(); 
		this.operatorUser.setId(operatorUserId);
	}
	
}
