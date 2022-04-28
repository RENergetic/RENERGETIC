package com.renergetic.backdb.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Getter
@Setter
@ToString
public class Asset {
	@JsonIgnore
	public static Map<String, AssetCategory> ALLOWED_TYPES;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", nullable = false, insertable = true, updatable = true)
	private String name;

	@ManyToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_type_id", nullable = false, insertable = true, updatable = true)
	private AssetType type;

	@Column(name = "label", nullable = true, insertable = true, updatable = true)
	private String label;

	@Column(name = "description", nullable = true, insertable = true, updatable = true)
	private String description;

	@Column(name = "geo_location", nullable = true, insertable = true, updatable = true)
	private String location;
	
	// REFERENCES THIS TABLE TO GROUP ASSETS
	@OneToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "parent_asset_id", nullable = true, insertable = true, updatable = true)
	private Asset parentAsset;
	
	// FOREIGN KEY FROM USERS TABLE
	@OneToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "owner_id", nullable = true, insertable = true, updatable = true)
	private User owner;
	
	@ManyToMany(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinTable(
			name = "asset_connection",
			joinColumns = @JoinColumn(name = "asset_id", nullable = true, insertable = true, updatable = true),
			inverseJoinColumns = @JoinColumn(name = "connected_asset_id"))
	private List<Asset> assets;

	@OneToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "uuid", nullable = false, insertable = true, updatable = false)
	private UUID uuid;

	public Asset(String name, AssetType type, String label, String description, String location, Asset part_of_asset, User owner) {
		super();
		this.name = name;
		this.type = type;
		this.label = label;
		this.description = description;
		this.location = location;
		this.parentAsset = part_of_asset;
		this.owner = owner;
	}
	
	static {
		ALLOWED_TYPES =  new HashMap<>();

		ALLOWED_TYPES.put("user", AssetCategory.user);

		ALLOWED_TYPES.put("room", AssetCategory.structural);
		ALLOWED_TYPES.put("flat", AssetCategory.structural);
		ALLOWED_TYPES.put("building", AssetCategory.structural);
		ALLOWED_TYPES.put("energy island", AssetCategory.structural);
		
		ALLOWED_TYPES.put("ev charging station", AssetCategory.energy);
		ALLOWED_TYPES.put("generation plant", AssetCategory.energy);
		ALLOWED_TYPES.put("heatpump", AssetCategory.energy);
		ALLOWED_TYPES.put("gas boiler", AssetCategory.energy);
		ALLOWED_TYPES.put("co-generation unit", AssetCategory.energy);
		ALLOWED_TYPES.put("coal plant", AssetCategory.energy);
		ALLOWED_TYPES.put("pv plant", AssetCategory.energy);
		ALLOWED_TYPES.put("external heat grid", AssetCategory.energy);
		ALLOWED_TYPES.put("external electricity grid", AssetCategory.energy);
		ALLOWED_TYPES.put("solar thermal collector", AssetCategory.energy);
		
		ALLOWED_TYPES.put("steam", AssetCategory.infrastructure);
		ALLOWED_TYPES.put("district heating", AssetCategory.infrastructure);
		ALLOWED_TYPES.put("district cooling", AssetCategory.infrastructure);
		ALLOWED_TYPES.put("electricity", AssetCategory.infrastructure);
	}
}
