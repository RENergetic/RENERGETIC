package com.renergetic.backdb.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "asset_connection")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class AssetConnection {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional = false, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = false, insertable = true, updatable = true)
	private Asset asset;

	@ManyToOne(optional = false, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "connected_asset_id", nullable = false, insertable = true, updatable = true)
	private Asset connectedAsset;

	@Column(name = "connection_type", nullable = true, insertable = true, updatable = true)
	private ConnectionType connectionType;
}
