package com.renergetic.kpiapi.dao;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.model.MeasurementType;

import lombok.Data;

@Data
public class AbstractMeterDataDAO {	
	@JsonProperty(required = true)
	String name;
	
	@JsonProperty(required = true)
	Domain domain;
	
	@JsonProperty(required = false)
	MeasurementType unit;
	
	@JsonProperty(required = true)
	Map<Long, Double> data;
	
	public AbstractMeterDataDAO() {
		data = new TreeMap<>();
	}
}
