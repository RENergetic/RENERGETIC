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

    s("s", "seconds", "base", "Time"),
    m("m", "metre", "base", "Distance"),
    kg("kg", "kilogram", "base", "Mass"),
    A("A", "ampere", "base", "Electric Current"),
    K("K", "kelvin", "base", "Temperature"),
    mol("mol", "mole", "base", "Amount of Substance"),
    cd("cd", "candela", "base", "Luminous Intensity"),

    W("W", "watt", "derived", "Power"),
    Wh("Wh", "watt/hour", "derived", "Energy"),
    J("J", "joule", "derived", "Energy"),
    V("V", "volt", "derived", "Voltage"),
    Ohm("Ohm", "ohm", "derived", "Electrical Resistance"),
    lx("Lx", "lux", "derived", "Illuminance"),
    gperkWh("g/kWh", "CO2 equivalent", "derived", "Contribution to the greenhouse effect in CO2"),
    m3perh("m3/h", "Flow", "derived", "Liquid flow"),

    percent("%", "Percentage", "other", "Percentage"),
    C("ºC", "Celsius", "other", "Temperature");

    public String SYMBOL;
    public String NAME;
    public String TYPE;
    public String DESCRIPTION;

    public static Optional<DashboardUnit> valueByName(String name) {
//    	String n = name.toLowerCase();
        return Arrays.stream(DashboardUnit.values()).filter(
                dashboardUnit -> dashboardUnit.getNAME().equals(name)).findFirst();
    }

    private DashboardUnit(String symbol, String name, String type, String description) {
        this.SYMBOL = symbol;
        this.NAME = name;
        this.TYPE = type;
        this.DESCRIPTION = description;
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
