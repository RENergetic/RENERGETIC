package com.renergetic.backdb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
	
	@Column(name = "category", nullable = true, insertable = true, updatable = true)
	@Enumerated(EnumType.STRING)
	@JsonProperty(required = false)
	private AssetCategory category;
	
	@Column(name = "renovable", nullable = true, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private Long renovable;

	public AssetType(long id, String name, String label, AssetCategory category, Long renovable) {
		super();
		this.id = id;
		this.name = name;
		this.label = label;
		this.category = category;
		this.renovable = renovable;
	}
}
