package com.renergetic.ingestionapi.dao;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MeasurementDAO {
	String measurement;
	Map<String, String> fields;
	Map<String, String> tags;
	
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value = "error", access = Access.READ_ONLY)
	String errorMessage;
}
