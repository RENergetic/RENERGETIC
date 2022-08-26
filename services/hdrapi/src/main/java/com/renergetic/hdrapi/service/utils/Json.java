package com.renergetic.hdrapi.service.utils;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONObject;

public class Json {


    public static JSONObject parse(String json) throws ParseException {
        if (json == null) {
            return new JSONObject();
        }
        JSONParser parser = new JSONParser(json);
        return (JSONObject) parser.parse();
    }

    public static String toJson(JSONObject json) {
        if (json != null) {
            return json.toString();
        }
        return "{}";
    }

}
