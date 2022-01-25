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
@Table(name = "infrastructureInformation")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InfrastructureInformation extends Information{
	// FOREIGN KEY FROM INFRASTRUCTURE TABLE
	@Column(name = "infrastructure_id")
	private long infrastructureId;

	public InfrastructureInformation(String name, String type, String unit, long infrastructure_id, long signal) {
		super(name, type, unit, signal);
		this.infrastructureId = infrastructure_id;
	}
}
