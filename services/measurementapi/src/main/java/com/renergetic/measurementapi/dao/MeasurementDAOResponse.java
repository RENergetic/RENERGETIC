package com.renergetic.measurementapi.dao;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MeasurementDAOResponse {
	String measurement;
	Map<String, String> fields;

	@JsonInclude(Include.NON_EMPTY)
	Map<String, String> tags;

	public MeasurementDAOResponse() {
		fields = new HashMap<>();
		tags = new HashMap<>();
	}
}
