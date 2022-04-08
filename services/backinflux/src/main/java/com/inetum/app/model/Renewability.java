package com.inetum.app.model;

import javax.persistence.Entity;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

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
	
	@Column(name = "asset_name", tag = true)
	@JsonProperty(access = Access.READ_ONLY, required = false, value = "asset_name")
	String assetName;
	
	public static String measurement() { return "renewability"; }
}
