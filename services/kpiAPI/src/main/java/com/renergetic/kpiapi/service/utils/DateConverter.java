package com.renergetic.kpiapi.service.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.renergetic.kpiapi.exception.InvalidArgumentException;

public final class DateConverter {
    //TODO: Raul set date format as you wish :)
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static ZoneId getTimezone() {
        return ZoneId.systemDefault();
    }

    public static Long toEpoch(LocalDateTime localDateTime) {
        return localDateTime.atZone(getTimezone()).toInstant().toEpochMilli();
    }

    public static Long toEpoch(Date date) {
        return date.getTime();
    }

    public static Long toEpoch(String date) {
    	try {
    		return dateFormat.parse(date).toInstant().toEpochMilli();
    	} catch (ParseException ex) {
			throw new InvalidArgumentException("The date %s can't be pased to epoch");
		}
    }

    public static LocalDateTime toLocalDateTime(Long epoch) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), getTimezone());
    }

    public static Date toDate(Long epoch) {
        return new Date(epoch);
    }

    public static String toString(long epoch) {
        return dateFormat.format(new Date(epoch));
    }

    public static String toString(Date date) {
        return dateFormat.format(date);
    }

    public static String toString(LocalDateTime localDateTime) {
        return dateFormat.format(localDateTime);
    }

}