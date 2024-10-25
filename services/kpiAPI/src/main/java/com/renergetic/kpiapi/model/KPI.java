package com.renergetic.kpiapi.model;

import com.renergetic.kpiapi.exception.InvalidArgumentException;

public enum KPI {
    ESS("ESS", "Energy Self Sufficiency","ratio_norm","ratio"),
    EP("EP", "Energy Potency","ratio","ratio"),
    ESC("ESC", "Energy self consumption","ratio","ratio"),
    EE("EE", "Energy Efficiency","ratio_norm","ratio"),
    ES("ES", "Energy Saving","ratio","ratio"),
    SRES("SRES", "Share of RES","ratio_norm","ratio"),
    SNES("SNES", "Share of non-RES","ratio_norm","ratio"),
    CO2("CO2", "CO2 Intensity","ratio","ratio"),
    PEAK("PEAK", "Peak value","energy","energy");

    public final String kpi;
    public final String description;
    public final String typeName;
    public final String physicalName;

    KPI(String name, String description, String typeName,String physicalName) {
        this.kpi = name;
        this.description = description;
        this.typeName = typeName;
        this.physicalName=physicalName;
    }

    public static KPI obtain(String name) {
        for (KPI meter : KPI.values()) {
            if (meter.name().equalsIgnoreCase(name) || meter.kpi.equalsIgnoreCase(name) || meter.description.equalsIgnoreCase(name))
                return meter;
        }

        throw new InvalidArgumentException("%s is not a valid KPI", name);
    }
}
