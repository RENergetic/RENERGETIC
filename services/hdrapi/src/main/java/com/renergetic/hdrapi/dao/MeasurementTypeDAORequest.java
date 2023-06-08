package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class MeasurementTypeDAORequest {
	//TODO: store symbol ,"ºC",
	@JsonProperty(required = false)
	private Long id;

	@JsonProperty(required = true,value = "physical_name")
	private String physicalName;

}
