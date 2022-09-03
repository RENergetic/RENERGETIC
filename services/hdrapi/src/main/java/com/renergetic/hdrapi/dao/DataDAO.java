package com.renergetic.hdrapi.dao;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataDAO {


	@JsonProperty(value = "current", required = false)
	private Data current;

	@JsonProperty(value = "prediction", required = false)
	private Data prediction;

	public DataDAO() {
		current = new Data();
		prediction = new Data();
	}
	
	@Getter
	@Setter
	public class Data {
		@JsonProperty(value = "last", required = false)
		private Map<String, Double> last;
		
		@JsonProperty(value = "max", required = false)
		private Map<String, Double> max;

		public Data() {
			last = new HashMap<>();
			max = new HashMap<>();
		}
	}
}