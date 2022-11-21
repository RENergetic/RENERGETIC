package com.renergetic.measurementapi.model;

public enum InfluxTimeUnit {
	ns (86400000000000., "nanosecond"),
	u (86400000000., "microsecond"),
	ms (86400000, "millisecond"),
	s (86400, "second"),
	m (1440, "minute"),
	h (24, "hour"),
	d (1, "day"),
	w (0.142857143, "week"),
	M (0.0328767, "month"),
	y (0.002739728002438356, "year");
	
	final double VALUE;
	final String NAME;
	
	InfluxTimeUnit(double value, String name) {
		VALUE = value;
		NAME = name;
    }
	
	public static boolean exists(String timeUnit) {
		for (InfluxTimeUnit elem : InfluxTimeUnit.values())
			if (elem.toString().equals(timeUnit.toLowerCase()))
				return true;
		return false;
	}
	
	public static boolean validate(String time) {
		if (time.matches("\\d+(\\.*\\d+)?\\D+"))
			for (InfluxTimeUnit elem : InfluxTimeUnit.values())
				if (time.contains(elem.name()))
					return true;
		return false;
	}
	
	public static double convert(long num, InfluxTimeUnit from, InfluxTimeUnit to) {
			return num * to.VALUE / from.VALUE;
	}
	
	public static String convert(String time, InfluxTimeUnit to) {
		double value;
		
		if (time.matches("\\d+(\\.*\\d+)?\\D"))
			value = Double.parseDouble(time.substring(0, time.length() - 1));
		else if (time.matches("\\d+(\\.*\\d+)?\\D{2}"))
			value = Double.parseDouble(time.substring(0, time.length() - 2));
		else return "0" + to.name();

		for (InfluxTimeUnit elem : InfluxTimeUnit.values())
			if (time.contains(elem.name()))
				return Math.round(value * to.VALUE / elem.VALUE) + to.name();
		
		return "0" + to.name();
	}
	
	public static Long convertNumber(String time, InfluxTimeUnit to) {
		double value;
		
		if (time.matches("\\d+(\\.*\\d+)?\\D"))
			value = Double.parseDouble(time.substring(0, time.length() - 1));
		else if (time.matches("\\d+(\\.*\\d+)?\\D{2}"))
			value = Double.parseDouble(time.substring(0, time.length() - 2));
		else return 0L;

		for (InfluxTimeUnit elem : InfluxTimeUnit.values())
			if (time.contains(elem.name()))
				return Math.round(value * to.VALUE / elem.VALUE);
		
		return 0L;
	}
	
	@Override
	public String toString() {
		return this.name();
	}
}
