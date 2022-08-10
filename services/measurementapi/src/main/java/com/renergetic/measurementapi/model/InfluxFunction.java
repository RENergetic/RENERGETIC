package com.renergetic.measurementapi.model;

public enum InfluxFunction {
	COUNT,
	DISTINCT,
	MEAN,
	MEDIAN,
	SUM,
	MAX,
	MIN,
	FIRST,
	LAST;
	
	public static InfluxFunction obtain(String function) {
		try {
			return InfluxFunction.valueOf(function.toUpperCase());
		}catch (IllegalArgumentException e) {
			return null;
		}
	}
}
