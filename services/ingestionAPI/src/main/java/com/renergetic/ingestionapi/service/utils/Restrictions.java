package com.renergetic.ingestionapi.service.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
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

		ArrayList<String> errors = new ArrayList<>();
		for (MeasurementDAO measurement : measurements) {
			// Check measurement name
			isValid = restrictions.getMeasurements().contains(measurement.getMeasurement().toLowerCase());

			// Check if tags are valid
			if (isValid) {
				measurement.getTags().forEach((key, value) -> {
					if (key != null && restrictions.getTags().containsKey(key)) {
						String valueFilter = restrictions.getTags().get(key);
						if (valueFilter != null && !value.matches(valueFilter))
							errors.add(String.format("'%s' isn't a valid value to the tag '%s'", value, key));
					} else {
						errors.add(String.format("'%s' isn't a valid tag name", key));
					}
				});
				if (!errors.isEmpty()) {
					isValid = false;
					measurement.setErrorMessage("Invalid tag name or value: " + errors.stream().collect(Collectors.joining(", ")));
					errors.clear();
				}
			} else measurement.setErrorMessage("Invalid measurement name: " + measurement.getMeasurement());
			// Check if fields are valid
			if (isValid) {
				measurement.getFields().forEach((key, value) -> {
					if (!key.equals("time")) {
						FieldRestrictionsDAO restriction = restrictions.getFields().stream().filter(fieldRestr -> fieldRestr.getName().equals(key)).findAny().orElse(null);
						if (restriction != null) {
							if (Boolean.TRUE.equals(!restriction.getType().validate(value)) || !(restriction.getFormat() == null || value.matches(restriction.getFormat())))
								errors.add(String.format("'%s' isn't a valid value to field '%s'", value, key));
						} else {
							errors.add(String.format("'%s' isn't a valid field name", key));
						}
					}
				});

				if (!errors.isEmpty()) {
					isValid = false;
					measurement.setErrorMessage("Invalid field name or value: " + errors.stream().collect(Collectors.joining(", ")));
					errors.clear();
				}
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
			field = new AbstractMap.SimpleEntry<>(key, "none");
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
