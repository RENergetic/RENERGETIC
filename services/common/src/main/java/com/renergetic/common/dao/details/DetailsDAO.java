package com.renergetic.common.dao.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
