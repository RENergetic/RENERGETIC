package com.renergetic.ingestionapi.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Subselect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Subselect("select * from asset")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Asset {	
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

	@Column(name = "geo_location", nullable = true, insertable = true, updatable = true)
	private String location;

	@OneToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_category_id", nullable = true, insertable = true, updatable = true)
	private AssetCategory assetCategory;
}
