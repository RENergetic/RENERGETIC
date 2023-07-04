package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DataDAO {


	@JsonProperty(value = "current", required = false)
	private Map<String, Map<String, Double>> current;

	@JsonProperty(value = "prediction", required = false)
	private Map<String, Map<String, Double>> prediction;

	public DataDAO() {
		current = new HashMap<>();
		prediction = new HashMap<>();
	}
	
	public void putCurrent(String function, String id, Double value) {
		if (!this.current.containsKey(function)) {
			this.current.put(function, new HashMap<>());
		}
		this.current.get(function).put(id, value);
	}
		
	public void putPrediction(String function, String id, Double value) {
		if (!this.prediction.containsKey(function)) {
			this.prediction.put(function, new HashMap<>());
		}
		this.prediction.get(function).put(id, value);
	}
}