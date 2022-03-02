package com.renergetic.backdb.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
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
	public static Map<String, String> ALLOWED_TYPES;
	
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
	@Setter
	@Column(name = "type", nullable = false, insertable = true, updatable = true)
	private String type;

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
	@OneToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "parent_asset_id", nullable = true, insertable = true, updatable = true)
	private Asset parentAsset;
	
	// FOREIGN KEY FROM USERS TABLE
	@OneToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "owner_id", nullable = true, insertable = true, updatable = true)
	private Asset owner;

	public Asset(String name, String type, String label, String description, String location, long part_of_asset_id, long owner_user_id) {
		super();
		this.name = name;
		this.type = type;
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

		ALLOWED_TYPES.put("user", "User");

		ALLOWED_TYPES.put("room", "Structural");
		ALLOWED_TYPES.put("flat", "Structural");
		ALLOWED_TYPES.put("building", "Structural");
		ALLOWED_TYPES.put("energy island", "Structural");
		
		ALLOWED_TYPES.put("ev charging station", "Energy");
		ALLOWED_TYPES.put("generation plant", "Energy");
		ALLOWED_TYPES.put("heatpump", "Energy");
		ALLOWED_TYPES.put("co-generation unit", "Energy");
		ALLOWED_TYPES.put("coal plant", "Energy");
		ALLOWED_TYPES.put("pv plant", "Energy");
		ALLOWED_TYPES.put("external heat grid", "Energy");
		ALLOWED_TYPES.put("external electricity grid", "Energy");
		
		ALLOWED_TYPES.put("steam", "Infrastructure");
		ALLOWED_TYPES.put("distric heating", "Infrastructure");
		ALLOWED_TYPES.put("distric cooling", "Infrastructure");
		ALLOWED_TYPES.put("electricity", "Infrastructure");
	}

	public Asset getParentAsset() {
		return parentAsset;
	}

	public void setParentAsset(Long id) {
		if (id != null) {
			this.parentAsset = new Asset();
			this.parentAsset.setId(id);
		}
	}

	public Asset getOwner() {
		return owner;
	}

	public void setOwner(Long id) {
		if (id != null) {
			this.owner = new Asset();
			this.owner.setId(id);
		}
	}
	
}
