package com.renergetic.backdb.model.information;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.renergetic.backdb.model.Information;
import com.renergetic.backdb.model.UnitSI;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "connectionInformation")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ConnectionInformation extends Information{
	// FOREIGN KEY FROM CONNECTION TABLE
	@Column(name = "connection_id")
	private long connectionId;

	public ConnectionInformation(String name, String type, UnitSI unit, long connection_id, long signal) {
		super(name, type, unit, signal);
		this.connectionId = connection_id;
	}
}
