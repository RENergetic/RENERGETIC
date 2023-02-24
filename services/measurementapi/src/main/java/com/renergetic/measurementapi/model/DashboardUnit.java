package com.renergetic.measurementapi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
@ToString
public enum DashboardUnit {

    s("s", "seconds", "base", "Time","time"),
    m("m", "metre", "base", "Distance","length"),
    kg("kg", "kilogram", "base", "Mass","mass"),
    A("A", "ampere", "base", "Electric Current","current"),
    K("K", "kelvin", "base", "Temperature","temperature"),
    mol("mol", "mole", "base", "Amount of Substance","substance_amount"),
    cd("cd", "candela", "base", "Luminous Intensity","luminous"),

    W("W", "watt", "derived", "Power","power"),
    Wh("Wh", "watt/hour", "derived", "Energy","energy"),
    J("J", "joule", "derived", "Energy","energy"),
    V("V", "volt", "derived", "Voltage","voltage"),
    Ohm("Ohm", "ohm", "derived", "Electrical Resistance","electric_resistance"),
    lx("Lx", "lux", "derived", "Illuminance","luminous"),
    gperkWh("g/kWh", "CO2 equivalent", "derived", "Contribution to the greenhouse effect in CO2","co2_energy"),
    m3perh("m3/h", "Flow", "derived", "Liquid flow","flow"),

    percent("%", "Percentage", "other", "Percentage","percentage"),
    C("ºC", "Celsius", "other", "Temperature","temperature");

    public String SYMBOL;
    public String NAME;
    public String TYPE;
    public String DESCRIPTION;
    public String PHYSICAL_TYPE;

    public static Optional<DashboardUnit> valueBySymbolAndPhysicalType(String symbol, String physicalType) {
        String physicalTypeLower = physicalType.toLowerCase();
        return Arrays.stream(DashboardUnit.values()).filter(
                dashboardUnit -> dashboardUnit.getSYMBOL().equals(symbol)
                        && dashboardUnit.getPHYSICAL_TYPE().equals(physicalTypeLower)).findFirst();
    }

    private DashboardUnit(String symbol, String name, String type, String description, String physicalType) {
        this.SYMBOL = symbol;
        this.NAME = name;
        this.TYPE = type;
        this.DESCRIPTION = description;
        this.PHYSICAL_TYPE=physicalType;
    }
}
