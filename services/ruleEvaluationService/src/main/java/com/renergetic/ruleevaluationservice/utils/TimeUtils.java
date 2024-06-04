package com.renergetic.ruleevaluationservice.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

    public static Long convertLiteralDiffToInstantMillis(String literalDiff){
        if(literalDiff.equals("now"))
            return Instant.now().toEpochMilli();
        else if(literalDiff.contains("now-"))
            return TimeUtils.offsetNegativeCurrentInstant(literalDiff.substring(4)).toEpochMilli();
        else if(literalDiff.contains("now+"))
            return TimeUtils.offsetPositiveCurrentInstant(literalDiff.substring(4)).toEpochMilli();
        return null;
    }

    public static Instant offsetNegativeCurrentInstant(String durationLiteral){
        durationLiteral = durationLiteral.replace(" ", "");
        Instant offset = Instant.now().minus(TimeUtils.extractValue(durationLiteral), TimeUtils.extractUnit(durationLiteral));
        return offset;
    }

    public static Instant offsetPositiveCurrentInstant(String durationLiteral){
        durationLiteral = durationLiteral.replace(" ", "");
        Instant offset = Instant.now().plus(TimeUtils.extractValue(durationLiteral), TimeUtils.extractUnit(durationLiteral));
        return offset;
    }

    public static Instant offsetCurrentInstantOfAtLeast3Hours(String durationLiteral){
        durationLiteral = durationLiteral.replace(" ", "");
        Instant now = Instant.now();
        Instant offset = Instant.now().minus(TimeUtils.extractValue(durationLiteral), TimeUtils.extractUnit(durationLiteral));
        if(ChronoUnit.HOURS.between(offset, now) < 3)
            offset = Instant.now().minus(3, ChronoUnit.HOURS);
        return offset;
    }

    public static ChronoUnit extractUnit(String durationLiteral){
        if(Character.isAlphabetic(durationLiteral.charAt(durationLiteral.length()-2))){
            switch(durationLiteral.substring(durationLiteral.length()-2)){
                case "ns":
                    return ChronoUnit.NANOS;
                case "ms":
                    return ChronoUnit.MILLIS;
            }
        } else {
            switch(durationLiteral.substring(durationLiteral.length()-1)){
                case "u":
                case "µ":
                    return ChronoUnit.MICROS;
                case "s":
                    return ChronoUnit.SECONDS;
                case "m":
                    return ChronoUnit.MINUTES;
                case "h":
                    return ChronoUnit.HOURS;
                case "d":
                    return ChronoUnit.DAYS;
                case "w":
                    return ChronoUnit.WEEKS;
            }
        }
        return null;
    }

    public static Long extractValue(String durationLiteral){
        if(Character.isAlphabetic(durationLiteral.charAt(durationLiteral.length()-2)))
            return Long.valueOf(durationLiteral.substring(0, durationLiteral.length()-2));
        else
            return Long.valueOf(durationLiteral.substring(0, durationLiteral.length()-1));
    }
}
