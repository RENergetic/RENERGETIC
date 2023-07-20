package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
