package com.inetum.app.service.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map.Entry;

import com.inetum.app.exception.InvalidArgumentException;

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
		else if (fieldString.getValue().matches("\\d+(\\.\\d)?\\d+")) {
			if (fieldString.getValue().contains("."))
				field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), Double.parseDouble(fieldString.getValue()));
			else
				field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), Long.parseLong(fieldString.getValue()));
		} else field = fieldString;
		
		return field;
	}
	
	public static Entry<String, String> parseSeries(Entry<String, Object> fieldString) {
		Entry<String, String> field;

		// CHECK IF IS A NULL
		if (fieldString.getValue() == null)
			field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), null);
		// CHECK IF IS A BOOLEAN
		else if (fieldString.getValue() instanceof Boolean) 
			field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), fieldString.getValue().toString());
		// CHECK IF IS A DATE
		else if (fieldString.getKey().startsWith("time") && fieldString.getValue() instanceof Double) {
			Date date = new Date(((Double) fieldString.getValue()).longValue());
			String text = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), text);
		} 
		// CHECK IF IS A NUMBER
		else if (fieldString.getValue() instanceof Double) {
			field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), new DecimalFormat("#.#############").format(fieldString.getValue()));
		} else field = new AbstractMap.SimpleEntry<>(fieldString.getKey(), fieldString.getValue().toString());
		
		return field;
	}
}
