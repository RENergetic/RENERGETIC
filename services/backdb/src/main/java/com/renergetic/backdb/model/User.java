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
@Table(name = "people")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class User {	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "name")
	private String name;
	
	// FOREIGN KEY FROM ASSET TABLE (ASSET WHERE USER RESIDE)
	@Column(name = "reside_asset_id")
	private long resideAssetId;

	public User(String name, long reside_asset_id) {
		super();
		this.name = name;
		this.resideAssetId = reside_asset_id;
	}
	
	
}
