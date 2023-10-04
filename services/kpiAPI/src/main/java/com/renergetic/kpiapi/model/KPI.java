package com.renergetic.kpiapi.model;

import com.renergetic.kpiapi.exception.InvalidArgumentException;

public enum KPI {
	ESS ("ESS", "Energy Self Sufficiency"),
	EP ("EP", "Energy Potency"),
	EE ("EE", "Energy Efficiency"),
	ES ("ES", "Energy Saving"),
	SRES ("SRES", "Share of RES"),
	SNES ("SNES", "Share of non-RES"),
	CO2 ("CO2", "CO2 Intensity"),
	PEAK ("PEAK", "Peak value");

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
