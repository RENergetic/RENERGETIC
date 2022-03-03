package com.renergetic.backdb.model;

public enum Unit {
	s ("s", "seconds", "base", "Time"),
	m ("m", "metre", "base", "Distance"),
	kg ("kg", "kilogram", "base", "Mass"),
	A ("A", "ampere", "base", "Electric Current"),
	K ("K", "kelvin", "base", "Temperature"),
	mol ("mol", "mole", "base", "Amount of Substance"),
	cd ("cd", "candela", "base", "Luminous Intensity"),
	
	W ("W", "watt", "derived", "Power"),
	kW ("kW", "kilowatt", "derived", "Power"),
	Wh ("Wh", "watt/hour", "derived", "Power"),
	kWh ("kWh", "kilowatt/hour", "derived", "Power"),
	V ("V", "volt", "derived", "Voltage"),
	Ohm ("Ohm", "ohm", "derived", "Electrical Resistance"),
	lx ("Lx", "lux", "derived", "Illuminance"),
	
	percent ("%", "Percentage", "other", "Percentage"),
	CO2e ("CO2e", "CO2 equivalent", "other", "Contribution to the greenhouse effect in CO2"),
	C ("ºC", "Celsius", "other", "Temperature");

	public String SYMBOL;
	public String NAME;
	public String TYPE;
	public String DESCRIPTION;
	
	private Unit(String symbol, String name, String type, String description) {
		this.SYMBOL = symbol;
		this.NAME = name;
		this.TYPE = type;
		this.DESCRIPTION = description;
	}
}
