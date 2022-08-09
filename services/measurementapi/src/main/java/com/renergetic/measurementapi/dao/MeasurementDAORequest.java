package com.inetum.app.dao;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MeasurementDAORequest {
	String bucket;
	String measurement;
	Map<String, String> fields;
	Map<String, String> tags;
}
