package com.renergetic.hdrapi.service.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.renergetic.hdrapi.model.DashboardUnit;
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

    public static JSONObject parse(String json) throws ParseException {
        if (json == null) {
            return new JSONObject();
        }
        return new JSONObject(json);
    }

    //    public static String toJson(JSONObject json) {
//        if (json != null) {
//            return json.toString();
//        }
//        return "{}";
//    }
    public static String toJson(Object obj) {

        if (obj != null) {
            return gson.toJson(obj);
        }
        return "{}";
    }

}
