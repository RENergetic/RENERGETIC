package com.renergetic.kpiapi.dao;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.AbstractMeterConfig;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.model.MeasurementType;

import lombok.Data;

@Data
public class AbstractMeterDataDAO {	
	@JsonProperty(required = true)
	AbstractMeter name;
	
	@JsonProperty(required = true)
	Domain domain;
	
	@JsonProperty(required = false)
	MeasurementType unit;
	
	@JsonProperty(required = true)
	Map<Long, Double> data;
	
	public AbstractMeterDataDAO() {
		data = new TreeMap<>();
	}
	
	public static AbstractMeterDataDAO create(AbstractMeterConfig config) {
		AbstractMeterDataDAO data = new AbstractMeterDataDAO();		
		data.setName(config.getName());
		data.setDomain(config.getDomain());
		
		return data;
	}
}
