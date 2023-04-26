package com.renergetic.userapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "asset_type")
@RequiredArgsConstructor
@ToString
public class AssetType {
	@Id
	@JsonProperty(required = true)
	@Column(name = "id", nullable = false, insertable = true, updatable = true, unique = true)
	private Long id;
	
	@Column(name = "name", nullable = false, insertable = true, updatable = true, unique = true)
	@JsonProperty(required = true)
	private String name;
	
	@Column(name = "label", nullable = true, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private String label;
	
	@Column(name = "renovable", nullable = true, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private Long renovable;

	public AssetType(long id, String name, String label,  Long renovable) {
		super();
		this.id = id;
		this.name = name;
		this.label = label;
		this.renovable = renovable;
	}
	
	public AssetType( String name  ) {
		super();
		this.name = name;
		this.label = name;
	}
	
	public AssetType(long id, String name, String label) {
		super();
		this.id = id;
		this.name = name;
		this.label = label;
	}
}
