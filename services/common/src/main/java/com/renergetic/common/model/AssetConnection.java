package com.renergetic.common.model;

import com.renergetic.common.model.listeners.AssetConnectionListener;
import com.renergetic.common.model.listeners.AssetDetailsListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@EntityListeners(AssetConnectionListener.class)
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
