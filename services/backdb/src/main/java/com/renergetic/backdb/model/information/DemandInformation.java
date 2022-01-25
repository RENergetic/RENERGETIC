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
@Table(name = "demandInformation")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class DemandInformation extends Information{
	// FOREIGN KEY FROM CONNECTION TABLE
	@Column(name = "demand_id")
	private long demandId;

	public DemandInformation(String name, String type, String unit, long demand_id, long signal) {
		super(name, type, unit, signal);
		this.demandId = demand_id;
	}
}
