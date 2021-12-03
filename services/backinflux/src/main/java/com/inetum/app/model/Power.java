package com.inetum.app.model;

import javax.persistence.Entity;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Entity
@Measurement(name = "myMeasurement")
public class Power {
	@Column(name = "time")
	long time;
	
	@Column(name = "power")
	long power;
	
	public Power() {
		
	}
	
	public Power(long time, long power) {
		this.time = time;
		this.power = power;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getPower() {
		return power;
	}

	public void setPower(long power) {
		this.power = power;
	}
	
}
