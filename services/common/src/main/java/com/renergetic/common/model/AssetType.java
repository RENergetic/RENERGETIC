package com.renergetic.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
	
//	@Column(name = "category", nullable = true, insertable = true, updatable = true)
//	@Enumerated(EnumType.STRING)
//	@JsonProperty(required = false)
//	private AssetTypeCategory typeCategory;
	
	@Column(name = "renovable", nullable = true, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private Long renovable;

	public AssetType(long id, String name, String label,  Long renovable) {//AssetTypeCategory typeCategory,
		super();
		this.id = id;
		this.name = name;
		this.label = label;
//		this.typeCategory = typeCategory;
		this.renovable = renovable;
	}
	public AssetType( String name  ) {//AssetTypeCategory typeCategory,
		super();
		this.name = name;
		this.label = name;
	}
	public AssetType(long id, String name, String label) {//, AssetTypeCategory typeCategory
		super();
		this.id = id;
		this.name = name;
		this.label = label;
//		this.typeCategory = typeCategory;
	}
}
