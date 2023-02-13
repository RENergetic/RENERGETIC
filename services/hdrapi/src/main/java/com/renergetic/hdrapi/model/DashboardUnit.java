package com.renergetic.hdrapi.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
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

    public static Optional<DashboardUnit> valueByName(String name) {
//    	String n = name.toLowerCase();
        return Arrays.stream(DashboardUnit.values()).filter(
                dashboardUnit -> dashboardUnit.getNAME().equals(name)).findFirst();
    }

    private DashboardUnit(String symbol, String name, String type, String description,String physicalType) {
        this.SYMBOL = symbol;
        this.NAME = name;
        this.TYPE = type;
        this.DESCRIPTION = description;
        this.PHYSICAL_TYPE=physicalType;
    }


//    public static final class DashboardUnitAdapter extends TypeAdapter<DashboardUnit> {
//        @Override
//        public DashboardUnit read(JsonReader reader) throws IOException {
//            reader.beginObject();
////			String fieldname = "";
//            DashboardUnit du = null;
//            while (reader.hasNext()) {
//                JsonToken token = reader.peek();
//
//                if (token.equals(JsonToken.NAME)) {
//                    //get the current token
//                    String fieldname = reader.nextName();
//
//                    if ("name".equals(fieldname)) {
//                        //move to next token
//                        reader.peek();
//                        du = DashboardUnit.valueByName(reader.nextString()).get();
//                    }
//                }
//
//
//            }
//            reader.endObject();
//            return du;
//
//        }
//
//        @Override
//        public void write(JsonWriter writer, DashboardUnit du) throws IOException {
//            writer.beginObject();
//            writer.name("name");
//            writer.value(du.NAME);
//            writer.name("symbol");
//            writer.value(du.SYMBOL);
//            writer.name("type");
//            writer.value(du.TYPE);
////			writer.name("description");
////			writer.value(du.DESCRIPTION);
//            writer.endObject();
//        }
//    }


}
