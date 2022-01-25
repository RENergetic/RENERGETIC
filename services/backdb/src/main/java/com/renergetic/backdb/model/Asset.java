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
@Table(name = "asset")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Asset {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "geo_location")
	private String location;
	
	// REFERENCES THIS TABLE TO GROUP ASSETS
	@Column(name = "part_of_asset_id")
	private long partOfAssetId;
	
	// FOREIGN KEY FROM USERS TABLE
	@Column(name = "owner_user_id")
	private long ownerUserId;

	public Asset(String name, String description, String location, long part_of_asset_id, long owner_user_id) {
		super();
		this.name = name;
		this.description = description;
		this.location = location;
		this.partOfAssetId = part_of_asset_id;
		this.ownerUserId = owner_user_id;
	}
}
