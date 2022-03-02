package com.renergetic.backdb.model.details;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Details;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "asset_details")
@RequiredArgsConstructor
@ToString
public class AssetDetails extends Details{
	// FOREIGN KEY FROM CONNECTION TABLE
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = false, insertable = true, updatable = true)
	private Asset asset;

	public AssetDetails(String key, String value, long asset_id) {
		super(key, value);
		this.asset.setId(asset_id);
	}

	public Long getAsset() {
		return asset.getId();
	}

	public void setAsset(Long id) {
		this.asset = new Asset();
		this.asset.setId(id);
	}
}
