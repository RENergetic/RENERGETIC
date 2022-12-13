package com.renergetic.ingestionapi.service.utils;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.renergetic.ingestionapi.dao.FieldRestrictionsDAO;
import com.renergetic.ingestionapi.dao.MeasurementDAO;
import com.renergetic.ingestionapi.dao.RestrictionsDAO;
import com.renergetic.ingestionapi.model.PrimitiveType;

public class Restrictions {
	
	/**
	 * Return a map with all measurements
	 * If the measurement meet all requirements its map value is true in other case false
	 * @param measurements List of Influx measurements
	 * @param restrictions List of restrictions to apply to measurements
	 * @return A map with measurements and if its meet the requirements
	 */
	public static Map<MeasurementDAO, Boolean> check(List<MeasurementDAO> measurements, RestrictionsDAO restrictions) {
		Map<MeasurementDAO, Boolean> ret = new HashMap<>();
		boolean isValid = true;
		
		for (MeasurementDAO measurement : measurements) {
			// Check measurement name
			isValid = restrictions.getMeasurements().contains(measurement.getMeasurement().toLowerCase());
			
			// Check if tags are valid
			if (isValid) {
				for(Entry<String, String> tag : measurement.getTags().entrySet()) {
					isValid = restrictions.getTags().containsKey(tag.getKey()) && 
							(restrictions.getTags().get(tag.getKey()) == null || 
							tag.getValue().matches(restrictions.getTags().get(tag.getKey())));
				}
				if (!isValid) measurement.setErrorMessage("Invalid tag name or value");
			} else measurement.setErrorMessage("Invalid measurement name");
			// Check if fields are valid
			if (isValid) {
				for(Entry<String, String> field : measurement.getFields().entrySet()) {
					isValid = restrictions.getFields().stream().anyMatch(restriction -> {
						return restriction.getName().equalsIgnoreCase(field.getKey()) &&
								(
									(restriction.getType().equals(PrimitiveType.DOUBLE) && field.getValue().matches("^-?\\d+(.\\d+)?$")) ||
									(restriction.getType().equals(PrimitiveType.INTEGER) && field.getValue().matches("^-?\\d+$")) ||
									(restriction.getType().equals(PrimitiveType.UNSIGNED_DOUBLE) && field.getValue().matches("^\\d+(.\\d+)?$")) ||
									(restriction.getType().equals(PrimitiveType.UNSIGNED_INTEGER) && field.getValue().matches("^\\d+$")) ||
									(restriction.getType().equals(PrimitiveType.BOOLEAN) && field.getValue().matches("(true) | (false)")) ||
									(restriction.getType().equals(PrimitiveType.STRING) && (restriction.getFormat() == null || field.getValue().matches(restriction.getFormat())))
								);
					});
				}
				if (!isValid) measurement.setErrorMessage("Invalid field name or value");
			}
			ret.put(measurement, isValid);
		}
		return ret;
	}
	
	public static Entry<String, ?> parseField(String key, String value, RestrictionsDAO restrictions) {
		Entry<String, ?> field;
		Map<String, PrimitiveType> fieldRestrictions = restrictions.getFields().stream().collect(Collectors.toMap(FieldRestrictionsDAO::getName, FieldRestrictionsDAO::getType));
		
		// CHECK IF IS A NULL
		if (value == null)
			field = new AbstractMap.SimpleEntry<>(key, null);
		// CHECK IF IS A BOOLEAN
		else if (fieldRestrictions.get(key) == PrimitiveType.BOOLEAN && value.matches("(true)"))
			field = new AbstractMap.SimpleEntry<>(key, true);
		else if (fieldRestrictions.get(key) == PrimitiveType.BOOLEAN && value.matches("(false)"))
			field = new AbstractMap.SimpleEntry<>(key, false);
		// CHECK IF IS A INTEGER
		else if (fieldRestrictions.get(key) == PrimitiveType.INTEGER && value.matches("^-?\\d+$"))
			field = new AbstractMap.SimpleEntry<>(key, Long.parseLong(value));
		else if (fieldRestrictions.get(key) == PrimitiveType.DOUBLE && value.matches("^-?\\d+(.\\d+)?$"))
			field = new AbstractMap.SimpleEntry<>(key, Double.parseDouble(value));
		else if (fieldRestrictions.get(key) == PrimitiveType.UNSIGNED_INTEGER && value.matches("^-?\\d+$"))
			field = new AbstractMap.SimpleEntry<>(key, Math.abs(Long.parseLong(value)));
		else if (fieldRestrictions.get(key) == PrimitiveType.UNSIGNED_DOUBLE && value.matches("^-?\\d+(.\\d+)?$"))
			field = new AbstractMap.SimpleEntry<>(key, Math.abs(Double.parseDouble(value)));
		// IF IS A STRING
		else field = new AbstractMap.SimpleEntry<>(key, value);;
		
		return field;
	}
}
