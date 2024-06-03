package com.renergetic.common.utilities;

import com.renergetic.common.exception.InvalidTimeFormatException;

public class TimeFormatValidator {
    public static void validateTimeDifferenceFromNow(String value){
        if(value == null || !startsWithCorrectPrefix(value)
                || !endsWithCorrectTimeValue(value)
                || value.length() < 6 || !containsOnlyNumbers(value.substring(4, value.length()-1)))
            throw new InvalidTimeFormatException();
    }

    public static void validateTimeDuration(String value){
        if(value == null || !endsWithCorrectTimeValue(value) || value.length() < 2
                || !containsOnlyNumbers(value.substring(0, value.length()-1)))
            throw new InvalidTimeFormatException();
    }

    private static boolean startsWithCorrectPrefix(String value){
        return value.startsWith("now-") || value.startsWith("now+");
    }

    private static boolean endsWithCorrectTimeValue(String value){
        return value.endsWith("s") || value.endsWith("m") || value.endsWith("h")
                || value.endsWith("d");
    }

    private static boolean containsOnlyNumbers(String value){
        return value.matches("[0-9]+");
    }
}
