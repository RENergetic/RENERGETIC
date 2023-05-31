package com.renergetic.userapi.model;

public enum AbstractMeter {
	lrs ("LRS", "Local renewable sources"),
	lns ("LNS", "Local non renewable sources"),
	ers ("ERS", "External renewable sources"),
	ens ("ENS", "External non renewable sources"),
	losses ("Losses", "Energy losses"),
	load ("Load", "Energy load"),
	excess ("Excess", "Energy Excess"),
	storage ("Storage", "Energy Storage"),
	res ("Storage RES", "Renewable energy storage"),
	nonres ("Storage non RES", "Non renewable energy storage");
	
	public String name;
	public String description;
	
	AbstractMeter(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public static AbstractMeter get(String name) {
		for (AbstractMeter meter: AbstractMeter.values()) {
			if (meter.name().equalsIgnoreCase(name) || meter.name.equalsIgnoreCase(name))
				return meter;
		}
		
		return null;
	}
}
