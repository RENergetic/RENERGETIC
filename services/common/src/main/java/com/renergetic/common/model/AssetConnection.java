package com.renergetic.common.model;

import com.renergetic.common.model.listeners.AssetConnectionListener;
import com.renergetic.common.model.listeners.AssetDetailsListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

	@Enumerated(EnumType.STRING)
	@Column(name = "connection_type", nullable = true, insertable = true, updatable = true)
	private ConnectionType connectionType;
}
