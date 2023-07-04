package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TimeseriesDAO {
//TODO: might change the final structure
	@JsonProperty(value = "timestamps", required = false)
	private List<Long> timestamps;
	@JsonProperty(value = "current", required = false)
	private Map<String, List<Double>> current;


	@JsonProperty(value = "prediction", required = false)
	private Map<String,  List<Double>> prediction;


	public TimeseriesDAO() {
		timestamps = new ArrayList<>();
		current = new HashMap<>();
		prediction = new HashMap<>();
	}
	public TimeseriesDAO(List<Long> timestamps) {
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

	public void putCurrent(  String id, List<Double> values) {
		if(this.timestamps.size()!=values.size()){
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
	public void putPrediction(  String id, List<Double>  values) {
		if(this.timestamps.size()!=values.size()){
			throw new IllegalArgumentException("Values have different length to timestamps array");
		}
		this.prediction.put(id, values);
	}
}