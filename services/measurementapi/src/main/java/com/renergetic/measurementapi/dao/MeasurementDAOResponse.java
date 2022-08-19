package com.renergetic.measurementapi.dao;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class MeasurementDAOResponse {
	String measurement;
	Map<String, String> fields;
	Map<String, String> tags;
}
