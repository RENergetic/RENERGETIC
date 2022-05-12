package com.inetum.app.dao;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MeasurementDAOResponse {
	String measurement;
	Map<String, String> fields;
	Map<String, String> tags;
}
