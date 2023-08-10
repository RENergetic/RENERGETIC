package com.renergetic.kpiapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.AbstractMeterConfig;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.model.MeasurementType;
import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class DataDAO<T> {
	@JsonProperty(required = true)
	T name;

	@JsonProperty(required = true)
	Domain domain;

	@JsonProperty(required = false)
	MeasurementType unit;

	@JsonProperty(required = true)
	Map<Long, Double> data;

	public DataDAO() {
		data = new TreeMap<>();
	}
}
