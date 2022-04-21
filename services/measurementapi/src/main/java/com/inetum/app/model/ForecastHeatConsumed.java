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

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Measurement(name = "forecast_consumed")
public class ForecastHeatConsumed {
	@TimeColumn
	@Column(name = "time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	@JsonProperty(access = Access.READ_ONLY, required = false)
	Instant time;
	
	@Column(name = "power")
	Double power;

	@TimeColumn
	@Column(name = "time_prediction")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	@JsonProperty(value = "time_prediction", required = false)
	Instant timePred;
	
	@Column(name = "asset_name", tag = true)
	@JsonProperty(access = Access.READ_ONLY, required = false, value = "asset_name")
	String assetName;
	
	public static String measurement() { return "forecast_consumed"; }
}
