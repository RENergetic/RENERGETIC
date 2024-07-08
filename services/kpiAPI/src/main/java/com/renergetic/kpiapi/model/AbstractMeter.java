package com.renergetic.kpiapi.model;

import com.renergetic.kpiapi.exception.InvalidArgumentException;

public enum AbstractMeter {
	LRS ("LRS", "Local renewable sources"),
	LNS ("LNS", "Local non renewable sources"),
	ERS ("ERS", "External renewable sources"),
	ENS ("ENS", "External non renewable sources"),
	LOSSES ("Losses", "Energy losses"),
	LOAD ("Load", "Energy load"),
	EXCESS ("Excess", "Energy Excess"),
	STORAGE ("Storage", "Energy Storage"),
	RES ("Storage RES", "Renewable energy storage"),
	NONRES ("Storage non RES", "Non renewable energy storage"),
	CUSTOM ("Custom", "Custom abstract meter"); //Custom type to calculate own abstract meter

	public final String meter;
	public final String description;
	
	AbstractMeter(String name, String description) {
		this.meter = name;
		this.description = description;
	}
	
	public static AbstractMeter obtain(String name) {
		for (AbstractMeter meter: AbstractMeter.values()) {
			if (meter.name().equalsIgnoreCase(name) || meter.meter.equalsIgnoreCase(name))
				return meter;
		}
		
		throw new InvalidArgumentException("%s is not a valid meter", name);
	}
}
