package com.renergetic.measurementapi.service.utils;

import com.renergetic.common.utilities.DateConverter;

import java.util.List;

public class InfluxAggregation {

    private static final long BASE_GRANULARITY_SEC =    60L * 10L;
    private static final long TIMESPAN_BASE_SEC = BASE_GRANULARITY_SEC * 2000;
    private static final long TIMESPAN_1h_SEC = (   60L * 60L) * 1000;
    private static final long TIMESPAN_12h_SEC = (    60L * 60L * 12) * 750;
    private static final long TIMESPAN_24h_SEC = (    60L * 60L * 24) * 500;

    public static List<String> setAggregation(List<String> query, long from, long to, String measurementType) {
        return setAggregation(query, from, to, measurementType, true);
    }

    public static List<String> setAggregation(List<String> query, String from, String to, String measurementType) {
        return setAggregation(query, from, to, measurementType, true);

    }

    public static List<String> setAggregation(List<String> query, String from, String to, String measurementType,
                                              boolean cumulativeMeter) {
        var toLong = to != null && !to.isEmpty() ? Long.parseLong(to) : DateConverter.now()/1000;
        return setAggregation(query, Long.parseLong(from), toLong, measurementType, cumulativeMeter);
    }

    public static List<String> setAggregation(List<String> query, long from, long to, String measurementType,
                                              boolean cumulativeMeter) {

        var period = getWindowPeriod(to-from);
        if (period != null) {
            var aggFunction = getAggFunc(measurementType, cumulativeMeter);

            query.add(String.format("aggregateWindow(every: %s, fn: %s, createEmpty: false)", period, aggFunction));
        }
        return query;


    }

    private static String getAggFunc(String measurementType, boolean cumulativeMeter) {
        if (measurementType.toLowerCase().contains("energy") && cumulativeMeter) {
            return "max";
        }
        if (measurementType.toLowerCase().contains("energy")) {
            return "sum";
        }
        return "mean";
    }

    private static String getWindowPeriod(long diff) {
        if (diff <= TIMESPAN_BASE_SEC)
            return null;
        if (diff <= TIMESPAN_1h_SEC)
            return "1h";
        if (diff <= TIMESPAN_12h_SEC)
            return "12h";
//    if(diff<=TIMESPAN_24h_MS)
        return "24h";
    }
}
