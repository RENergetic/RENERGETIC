package com.inetum.app.model;

import javax.persistence.Entity;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Measurement(name = "renowability")
public class Renewability {
	@Column(name = "time")
	Long time;
	
	@Column(name = "typeRen")
	String typeRen;
	
	@Column(name = "value")
	Long value;
	
	public static String measurement() { return "renewability"; }
}
