package com.renergetic.hdrapi.dao.details;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DetailsDAO {
	@JsonProperty(required = true)
	private String key;

	@JsonProperty(required = true)
	private String value;

	public DetailsDAO(String key, String value) {
		this.key = key;
		this.value = value;
	}
}
