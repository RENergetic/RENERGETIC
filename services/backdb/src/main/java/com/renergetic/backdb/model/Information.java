package com.renergetic.backdb.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@JsonIgnore
	public static List<String> ALLOWED_TYPES;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "property_name", nullable = true, insertable = true, updatable = true)
	private String name;
	
	@Column(name = "type", nullable = false, insertable = true, updatable = true)
	private String type;
	
	@Column(name = "unit", nullable = false, insertable = true, updatable = true)
	@Enumerated(EnumType.STRING)
	private UnitSI unit;
	
	// ID OF SIGNAL FROM TIMESERIES IN INFLUX DB
	@Column(name = "signal")
	private long signal;

	public Information(String name, String type, UnitSI unit, long signal) {
		super();
		this.name = name;
		this.type = type;
		this.unit = unit;
		this.signal = signal;
	}
	
	static {
		ALLOWED_TYPES =  new ArrayList<>();
		
		ALLOWED_TYPES.add("Constant");
		ALLOWED_TYPES.add("Time Series");
	}
}
