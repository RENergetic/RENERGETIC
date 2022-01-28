package com.renergetic.backdb.model;

import org.modelmapper.ModelMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class AssetRequest {
	@Getter
	@Setter
	private long id;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String type;

	@Getter
	@Setter
	private String description;

	@Getter
	@Setter
	private String location;
	
	// REFERENCES THIS TABLE TO GROUP ASSETS
	private Long partOfAsset;
	
	// FOREIGN KEY FROM USERS TABLE
	private Long ownerUser;

	public AssetRequest(String name, String type, String description, String location, long part_of_asset_id, long owner_user_id) {
		super();
		this.name = name;
		this.type = type;
		this.description = description;
		this.location = location;
		this.partOfAsset = part_of_asset_id;
		this.ownerUser = owner_user_id;
	}

	public Long getPartOfAsset() {
		return partOfAsset;
	}

	public void setPartOfAsset(Long id) {
		this.partOfAsset = id;
	}

	public Long getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(Long id) {
		this.ownerUser = id;
	}
	
	public Asset mapToEntity() {
		return new ModelMapper().map(this, Asset.class);
	}
}
