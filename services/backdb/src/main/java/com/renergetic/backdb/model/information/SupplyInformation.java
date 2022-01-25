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
@Table(name = "supplyInformation")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class SupplyInformation extends Information{
	// FOREIGN KEY FROM CONNECTION TABLE
	@Column(name = "supply_id")
	private long supplyId;

	public SupplyInformation(String name, String type, String unit, long supply_id, long signal) {
		super(name, type, unit, signal);
		this.supplyId = supply_id;
	}
}
