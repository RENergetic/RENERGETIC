package com.renergetic.measurementapi.service.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map.Entry;

import com.renergetic.measurementapi.exception.InvalidArgumentException;

public class FieldsFormat {
	
	public static Entry<String, ?> parseField(Entry<String, String> fieldString) {
		Entry<String, ?> field;

		// CHECK IF IS A NULL
		if (fieldString.getValue() == null)
			field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), null);
		// CHECK IF IS A BOOLEAN
		else if (fieldString.getValue().equalsIgnoreCase("true")) 
			field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), true);
		else if (fieldString.getValue().equalsIgnoreCase("false")) 
			field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), false);
		// CHECK IF IS A DATE
		else if (fieldString.getKey().startsWith("time")) {
			try {
				Instant time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldString.getValue()).toInstant();
				field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), time.toEpochMilli());
			} catch(ParseException error) {
				error.printStackTrace();
				throw new InvalidArgumentException("Invalid value to field " + fieldString.getKey());
			}
		} 
		// CHECK IF IS A NUMBER
		else if (fieldString.getValue().matches("-?\\d+(\\.\\d)?\\d*")) {
			if (fieldString.getValue().contains("."))
				field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), Double.parseDouble(fieldString.getValue()));
			else
				field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), Long.parseLong(fieldString.getValue()));
		} else field = fieldString;
		
		return field;
	}
	
	public static Entry<String, String> parseSeries(String key, Object value) {
		Entry<String, String> field;

		// CHECK IF IS A NULL
		if (value == null)
			field = new AbstractMap.SimpleEntry<>(key, null);
		// CHECK IF IS A BOOLEAN
		else if (value instanceof Boolean) 
			field = new AbstractMap.SimpleEntry<>(key, value.toString());
		// CHECK IF IS A DATE
		else if (key.startsWith("time")) {
			Date date = null;
			if (value instanceof Long)
				date = new Date((Long)value);
			else if (value instanceof Double)
				date = new Date(((Double) value).longValue());
			else if (value instanceof Instant)
				date = new Date(((Instant) value).toEpochMilli());
			
			String text = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			field = new AbstractMap.SimpleEntry<>(key, text);
		} 
		// CHECK IF IS A NUMBER
		else if (value instanceof Double) {
			field = new AbstractMap.SimpleEntry<>(key, new DecimalFormat("#.#############").format(value));
		} else field = new AbstractMap.SimpleEntry<>(key, value.toString());
		
		return field;
	}
}
