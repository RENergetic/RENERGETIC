package com.renergetic.common.model;

public enum InfluxFunction {
	count,
	distinct,
	mean,
	median,
	sum,
	max,
	min,
	first,
	last;
	
	public static InfluxFunction obtain(String function) {
		try {
			return InfluxFunction.valueOf(function.toLowerCase());
		}catch (IllegalArgumentException e) {
			return null;
		}
	}
}
