package com.renergetic.hdrapi.model.details;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.Details;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "asset_details")
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
