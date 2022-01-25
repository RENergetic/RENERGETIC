package com.renergetic.backdb.model.information;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.renergetic.backdb.model.Information;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "assetInformation")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class AssetInformation extends Information{
	// FOREIGN KEY FROM CONNECTION TABLE
	@Column(name = "asset_id")
	private long assetId;

	public AssetInformation(String name, String type, String unit, long asset_id, long signal) {
		super(name, type, unit, signal);
		this.assetId = asset_id;
	}
}
