package com.inetum.app.model;

import java.time.Instant;

import javax.persistence.Entity;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Measurement(name = "heat_supply")
public class HeatSupply {
	@TimeColumn
	@Column(name = "time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	@JsonProperty(access = Access.READ_ONLY, required = false)
	Instant time;
	
	@Column(name = "power")
	@JsonProperty(value = "value", required = true)
	Double power;
	
	@Column(name = "asset_name", tag = true)
	@JsonProperty(access = Access.READ_ONLY, required = false, value = "asset_name")
	String assetName;
	
	public static String measurement() { return "heat_supply"; }
}
