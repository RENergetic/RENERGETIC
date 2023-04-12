package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TimeseriesDAO {
//TODO: might change the final structure
	@JsonProperty(value = "timestamps", required = false)
	private Long[] timestamps;
	@JsonProperty(value = "current", required = false)
	private Map<String, Double[]> current;


	@JsonProperty(value = "prediction", required = false)
	private Map<String,  Double[]> prediction;


	public TimeseriesDAO() {
		current = new HashMap<>();
		prediction = new HashMap<>();
	}
	public TimeseriesDAO(Long[] timestamps) {
		this.timestamps = timestamps;
		current = new HashMap<>();
		prediction = new HashMap<>();
	}
	
//	public void putCurrent(String function, String id, Double value) {
//		if (!this.current.containsKey(function)) {
//			this.current.put(function, new HashMap<>());
//		}
//		this.current.get(function).put(id, value);
//	}

	public void putCurrent(  String id, Double[] values) {
		if(this.timestamps.length!=values.length){
			throw new IllegalArgumentException("Values have different length to timestamps array");
		}
			this.current.put(id, values);
	}

//	public void putPrediction(String function, String id, Double value) {
//		if (!this.prediction.containsKey(function)) {
//			this.prediction.put(function, new HashMap<>());
//		}
//		this.prediction.get(function).put(id, value);
//	}
	public void putPrediction(  String id, Double[]  values) {
		if(this.timestamps.length!=values.length){
			throw new IllegalArgumentException("Values have different length to timestamps array");
		}
		this.prediction.put(id, values);
	}
}