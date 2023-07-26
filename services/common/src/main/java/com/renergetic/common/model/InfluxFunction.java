package com.renergetic.common.model;

/*
 * The UI works with the functions in lower case and using enums is easier to see the allowed values in the Swagger docs
 * Also, it allows to return a BAD REQUEST exception easier if this values are wrong
 */
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
