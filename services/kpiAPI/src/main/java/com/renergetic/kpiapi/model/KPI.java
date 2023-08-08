package com.renergetic.kpiapi.model;

import com.renergetic.kpiapi.exception.InvalidArgumentException;

public enum KPI {
	LRS ("ESS", "Energy Self Sufficiency"),
	LNS ("EP", "Energy Potency"),
	ERS ("EE", "Energy Efficiency"),
	ENS ("ES", "Energy Saving"),
	LOSSES ("Losses", "Energy losses"),
	LOAD ("SRES", "Share of RES"),
	EXCESS ("SNES", "Share of non-RES"),
	STORAGE ("CO2", "CO2 Intensity"),
	RES ("PEAK", "Peak value");

	public final String kpi;
	public final String description;

	KPI(String name, String description) {
		this.kpi = name;
		this.description = description;
	}
	
	public static KPI obtain(String name) {
		for (KPI meter: KPI.values()) {
			if (meter.name().equalsIgnoreCase(name) || meter.kpi.equalsIgnoreCase(name) || meter.description.equalsIgnoreCase(name))
				return meter;
		}
		
		throw new InvalidArgumentException("%s is not a valid KPI", name);
	}
}
