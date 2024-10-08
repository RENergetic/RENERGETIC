package com.renergetic.common.model.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.Details;
import com.renergetic.common.model.listeners.AssetDetailsListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name = "asset_details")
@EntityListeners(AssetDetailsListener.class)
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class AssetDetails extends Details{
	// FOREIGN KEY FROM CONNECTION TABLE
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = false, insertable = true, updatable = true)
	@JsonIgnore()
	private Asset asset;

	public AssetDetails(String key, String value, Asset asset) {
		super(key, value);
		this.asset = asset;
	}
}
