package com.renergetic.measurementapi.dao;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MeasurementDAOResponse {
	String measurement;
	Map<String, String> fields;
	Map<String, String> tags;

	public MeasurementDAOResponse() {
		fields = new HashMap<>();
		tags = new HashMap<>();
	}
}
