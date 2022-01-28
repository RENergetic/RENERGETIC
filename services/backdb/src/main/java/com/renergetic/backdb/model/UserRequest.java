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
public class UserRequest {	
	@Getter
	@Setter
	@JsonProperty(access = Access.READ_ONLY)
	private long id;

	@Getter
	@Setter
	private String name;
	
	// FOREIGN KEY FROM ASSET TABLE (ASSET WHERE USER RESIDE)
	private Long resideAsset;

	public UserRequest(String name, long reside_asset_id) {
		super();
		this.name = name;
		this.resideAsset = reside_asset_id;
	}

	public Long getResideAsset() {
		return resideAsset;
	}

	public void setResideAsset(Long reside_asset_id) {
		this.resideAsset = reside_asset_id;
	}
	
	public User mapToEntity() {
		return new ModelMapper().map(this, User.class);
	}
	
}
