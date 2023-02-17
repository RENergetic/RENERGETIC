package com.renergetic.hdrapi.service.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import org.apache.tomcat.util.json.ParseException;
import org.json.JSONObject;

public class Json {
    private static final Gson gson;

    static {
        GsonBuilder gb = new GsonBuilder();
//        gb.registerTypeAdapter(DashboardUnit.class, new DashboardUnit.DashboardUnitAdapter());
        gson = gb.create();
    }
//    public static JSONObject parse(String json) throws ParseException {
//        if (json == null) {
//            return new JSONObject();
//        }
//        return new JSONObject(json);
//    }

    public static JSONObject parse(Object json) throws ParseException {
    	String jsonStr;
    	
    	if (json == null)
            return new JSONObject();
        else if (json instanceof String)
        	jsonStr = (String) json;
        else if (json instanceof byte[])
        	jsonStr = new String((byte[]) json);
        else 
        	jsonStr = gson.toJson(json);
    	
        return new JSONObject(jsonStr);
    }

    public static Map<String, Object> toMap(Object json) throws ParseException {
        return parse(json).toMap();
    }

    //    public static String toJson(JSONObject json) {
//        if (json != null) {
//            return json.toString();
//        }
//        return "{}";
//    }
    public static String toJson(Object obj) {

        if (obj != null)
            return gson.toJson(obj);

        return "{}";
    }

}
