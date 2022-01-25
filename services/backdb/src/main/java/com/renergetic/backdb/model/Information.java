package com.renergetic.backdb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.apache.commons.lang3.NotImplementedException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*@Entity
@Table(name = "information")*/
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@MappedSuperclass
public class Information {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "property_name")
	private String name;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "unit")
	private String unit;
	
	// ID OF SIGNAL FROM TIMESERIES IN INFLUX DB
	@Column(name = "signal")
	private long signal;

	public Information(String name, String type, String unit, long signal) {
		super();
		this.name = name;
		this.type = type;
		this.unit = unit;
		this.signal = signal;
	}
}
