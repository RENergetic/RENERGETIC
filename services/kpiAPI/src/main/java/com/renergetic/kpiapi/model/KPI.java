package com.renergetic.kpiapi.model;

import com.renergetic.kpiapi.exception.InvalidArgumentException;

public enum KPI {
    ESS("ESS", "Energy Self Sufficiency","ratio_norm"),
    EP("EP", "Energy Potency","ratio"),
    ESC("ESC", "Energy self consumption","ratio"),
    EE("EE", "Energy Efficiency","ratio_norm"),
    ES("ES", "Energy Saving","ratio"),
    SRES("SRES", "Share of RES","ratio_norm"),
    SNES("SNES", "Share of non-RES","ratio_norm"),
    CO2("CO2", "CO2 Intensity","ratio"),
    PEAK("PEAK", "Peak value","energy");

    public final String kpi;
    public final String description;
    public final String typeName;

    KPI(String name, String description, String typeName) {
        this.kpi = name;
        this.description = description;
        this.typeName = typeName;
    }

    public static KPI obtain(String name) {
        for (KPI meter : KPI.values()) {
            if (meter.name().equalsIgnoreCase(name) || meter.kpi.equalsIgnoreCase(name) || meter.description.equalsIgnoreCase(name))
                return meter;
        }

        throw new InvalidArgumentException("%s is not a valid KPI", name);
    }
}
