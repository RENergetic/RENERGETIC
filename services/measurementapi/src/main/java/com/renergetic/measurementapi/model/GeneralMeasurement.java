package com.renergetic.measurementapi.model;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
//@Entity
public class GeneralMeasurement {
	private String name;
	private Map<String, ?> fields;
	private Map<String, String> tags;
}
