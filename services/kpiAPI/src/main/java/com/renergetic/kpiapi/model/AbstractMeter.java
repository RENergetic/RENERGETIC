package com.renergetic.kpiapi.model;

import com.renergetic.kpiapi.exception.InvalidArgumentException;

public enum AbstractMeter {
    LRS("LRS", "Local renewable consumption", "energy"),
    LNS("LNS", "Local non renewable consumption", "energy"),
    ERS("ERS", "External renewable consumption", "energy"),
    ENS("ENS", "External non renewable consumption", "energy"),
    LOSSES("Losses", "Energy losses", "energy"),
    LOAD("Load", "Energy load", "energy"),
    EXCESS("Excess", "Energy Excess", "energy"),
    STORAGE("Storage", "Energy Storage", "energy"),
    RES("Storage RES", "Renewable energy storage", "energy"),
    NONRES("Storage non RES", "Non renewable energy storage", "energy"),
    PLRS("PLRS", "Local renewable production", "energy"),
    PLNS("PLNS", "Local non renewable production", "energy"),
    CUSTOM("Custom", "Custom abstract meter", null); //Custom type to calculate own abstract meter

    public final String meterLabel;
    public final String description;
    public final String physicalName;

    AbstractMeter(String name,  String description, String physicalName) {

        this.meterLabel = name;
        this.description = description;
        this.physicalName = physicalName;
    }

    public static AbstractMeter obtain(String name) {
        for (AbstractMeter meter : AbstractMeter.values()) {
            if (meter.name().equalsIgnoreCase(name) || meter.meterLabel.equalsIgnoreCase(name))
                return meter;
        }

        throw new InvalidArgumentException("%s is not a valid meter", name);
    }
}
