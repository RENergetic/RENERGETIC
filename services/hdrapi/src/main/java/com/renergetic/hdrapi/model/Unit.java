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
	Wh ("Wh", "watt/hour", "derived", "Energy"),
	J ("J", "joule", "derived", "Energy"),
	V ("V", "volt", "derived", "Voltage"),
	Ohm ("Ohm", "ohm", "derived", "Electrical Resistance"),
	lx ("Lx", "lux", "derived", "Illuminance"),
	gperkWh ("g/kWh", "CO2 equivalent", "derived", "Contribution to the greenhouse effect in CO2"),
	m3perh ("m3/h", "Flow", "derived", "Liquid flow"),
	
	percent ("%", "Percentage", "other", "Percentage"),
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
