package com.renergetic.backdb.service.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class DateConverter {
    //TODO: Raul set date format as you wish :)
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("aaaa-MM-dd hh:mm:ss");

    private static ZoneId getTimezone() {
        return ZoneId.systemDefault();
    }

    public static Long toEpoch(LocalDateTime localDateTime) {
        return localDateTime.atZone(getTimezone()).toInstant().toEpochMilli();
    }

    public static Long toEpoch(Date date) {
        return date.getTime();
    }

    public LocalDateTime toLocalDateTime(Long epoch) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), getTimezone());
    }

    public Date toDate(Long epoch) {
        return new Date(epoch);
    }

    public String toString(long epoch) {
        return dateFormat.format(new Date(epoch));
    }

    public String toString(Date date) {
        return dateFormat.format(date);
    }

    public String toString(LocalDateTime localDateTime) {
        return dateFormat.format(localDateTime);
    }

}