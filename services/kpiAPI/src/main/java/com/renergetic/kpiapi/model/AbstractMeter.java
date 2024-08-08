package com.renergetic.kpiapi.model;

import com.renergetic.kpiapi.exception.InvalidArgumentException;

public enum AbstractMeter {
    LRS("LRS", "Local renewable sources", "energy"),
    LNS("LNS", "Local non renewable sources", "energy"),
    ERS("ERS", "External renewable sources", "energy"),
    ENS("ENS", "External non renewable sources", "energy"),
    LOSSES("Losses", "Energy losses", "energy"),
    LOAD("Load", "Energy load", "energy"),
    EXCESS("Excess", "Energy Excess", "energy"),
    STORAGE("Storage", "Energy Storage", "energy"),
    RES("Storage RES", "Renewable energy storage", "energy"),
    NONRES("Storage non RES", "Non renewable energy storage", "energy"),
    CUSTOM("Custom", "Custom abstract meter", null); //Custom type to calculate own abstract meter

    public final String meter;
    public final String description;
    public final String physicalName;

    AbstractMeter(String name, String description, String physicalName) {
        this.meter = name;
        this.description = description;
        this.physicalName = physicalName;
    }

    public static AbstractMeter obtain(String name) {
        for (AbstractMeter meter : AbstractMeter.values()) {
            if (meter.name().equalsIgnoreCase(name) || meter.meter.equalsIgnoreCase(name))
                return meter;
        }

        throw new InvalidArgumentException("%s is not a valid meter", name);
    }
}
