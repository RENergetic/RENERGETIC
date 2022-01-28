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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "asset")
@RequiredArgsConstructor
@ToString
public class Asset {
	@JsonIgnore
	public static List<String> ALLOWED_TYPES;
	
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
	@Column(name = "type", nullable = false, insertable = true, updatable = true)
	private String type;

	@Getter
	@Setter
	@Column(name = "description", nullable = true, insertable = true, updatable = true)
	private String description;

	@Getter
	@Setter
	@Column(name = "geo_location", nullable = true, insertable = true, updatable = true)
	private String location;
	
	// REFERENCES THIS TABLE TO GROUP ASSETS
	@OneToOne(optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "part_of_asset_id", nullable = true, insertable = true, updatable = true)
	private Asset partOfAsset;
	
	// FOREIGN KEY FROM USERS TABLE
	@OneToOne(optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "owner_user_id", nullable = true, insertable = true, updatable = true)
	private User ownerUser;

	public Asset(String name, String type, String description, String location, long part_of_asset_id, long owner_user_id) {
		super();
		this.name = name;
		this.type = type;
		this.description = description;
		this.location = location;
		this.partOfAsset = new Asset();
		this.partOfAsset.setId(part_of_asset_id);
		this.ownerUser = new User();
		this.ownerUser.setId(owner_user_id);
	}
	
	static {
		ALLOWED_TYPES =  new ArrayList<>();
		
		ALLOWED_TYPES.add("Flat");
		ALLOWED_TYPES.add("Building");
		ALLOWED_TYPES.add("Energy Island");
		ALLOWED_TYPES.add("EV Charging Station");
		ALLOWED_TYPES.add("Generation Plant");
		ALLOWED_TYPES.add("HeatPump");
		ALLOWED_TYPES.add("Co-generation Unit");
		ALLOWED_TYPES.add("Coal plant");
		ALLOWED_TYPES.add("PV Plant");
		ALLOWED_TYPES.add("External Heat Grid");
		ALLOWED_TYPES.add("External Electricity Grid");
	}

	public Asset getPartOfAsset() {
		return partOfAsset;
	}

	public void setPartOfAsset(Long id) {
		this.partOfAsset = new Asset();
		this.partOfAsset.setId(id);
	}

	public User getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(Long id) {
		this.ownerUser = new User();
		this.ownerUser.setId(id);
	}
	
}
