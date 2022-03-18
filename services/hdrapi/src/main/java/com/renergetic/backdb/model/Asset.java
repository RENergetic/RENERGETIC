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
@ToString
public class Asset {
	@JsonIgnore
	public static Map<String, AssetCategory> ALLOWED_TYPES;
	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Getter
	@Setter
	@Column(name = "uuid", nullable = true, insertable = true, updatable = true, unique = true)
	private String uuid;

	@Getter
	@Setter
	@Column(name = "name", nullable = false, insertable = true, updatable = true)
	private String name;

	@Getter
	@ManyToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_type_id", nullable = false, insertable = true, updatable = true)
	private AssetType type;

	@Getter
	@Setter
	@Column(name = "label", nullable = true, insertable = true, updatable = true)
	private String label;

	@Getter
	@Setter
	@Column(name = "description", nullable = true, insertable = true, updatable = true)
	private String description;

	@Getter
	@Setter
	@Column(name = "geo_location", nullable = true, insertable = true, updatable = true)
	private String location;
	
	// REFERENCES THIS TABLE TO GROUP ASSETS
	@Getter
	@OneToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "parent_asset_id", nullable = true, insertable = true, updatable = true)
	private Asset parentAsset;
	
	// FOREIGN KEY FROM USERS TABLE
	@Getter
	@OneToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "owner_id", nullable = true, insertable = true, updatable = true)
	private Asset owner;
	
	@Getter
	@Setter
	@ManyToMany(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinTable(
			name = "asset_connection",
			joinColumns = @JoinColumn(name = "asset_id", nullable = true, insertable = true, updatable = true),
			inverseJoinColumns = @JoinColumn(name = "connected_asset_id"))
	private List<Asset> assets;

	public Asset(String name, Long type, String label, String description, String location, Long part_of_asset_id, Long owner_user_id) {
		super();
		this.name = name;
		this.setType(type);
		this.label = label;
		this.description = description;
		this.location = location;
		this.parentAsset = new Asset();
		this.parentAsset.setId(part_of_asset_id);
		this.owner = new Asset();
		this.owner.setId(owner_user_id);
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

	public void setType(Long id) {
		if (id != null) {
			this.type = new AssetType();
			this.type.setId(id);
		}
	}

	public void setParentAsset(Long id) {
		if (id != null) {
			this.parentAsset = new Asset();
			this.parentAsset.setId(id);
		}
	}

	public void setOwner(Long id) {
		if (id != null) {
			this.owner = new Asset();
			this.owner.setId(id);
		}
	}
	
}
