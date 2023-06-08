package com.renergetic.kpiapi.model;

import com.renergetic.kpiapi.exception.InvalidArgumentException;

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
		if (function == null) {
			throw new InvalidArgumentException("Function can't be null");
		}
		try {
			return InfluxFunction.valueOf(function.toUpperCase());
		}catch (IllegalArgumentException e) {
			throw new InvalidArgumentException("%s is not a valid function", function);
		}
	}
}
