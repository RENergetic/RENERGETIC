package com.renergetic.backdb.model;

public enum UnitSI {
	s ("seconds", "base", "Time"),
	m ("metre", "base", "Distance"),
	kg ("kilogram", "base", "Mass"),
	A ("ampere", "base", "Electric Current"),
	K ("kelvin", "base", "Temperature"),
	mol ("mole", "base", "Amount of Substance"),
	cd ("candela", "base", "Luminous Intensity"),
	
	W ("watt", "derived", "Power"),
	kW ("kilowatt", "derived", "Power"),
	kWh ("kilowatt/hour", "derived", "Power"),
	V ("volt", "derived", "Voltage"),
	Ohm ("ohm", "derived", "Electrical Resistance"),
	lx ("lux", "derived", "Illuminance");
	
	public String NAME;
	public String TYPE;
	public String DESCRIPTION;
	
	private UnitSI(String name, String type, String description) {
		this.NAME = name;
		this.TYPE = type;
		this.DESCRIPTION = description;
	}
}
