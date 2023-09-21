
package com.renergetic.measurementapi.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.renergetic.measurementapi.dao.MeasurementDAOResponse;
import com.renergetic.measurementapi.service.utils.FieldsFormat;

public abstract class MeasurementMapper {

	private MeasurementMapper() {}

	/**
	 * Converts a list of FluxTable objects into a list of MeasurementDAOResponse objects.
	 *
	 * @param  tables  the list of FluxTable objects to convert
	 * @return         the list of MeasurementDAOResponse objects converted from the FluxTable objects
	 */
	public static List<MeasurementDAOResponse> fromFlux(List<FluxTable> tables) {
		ArrayList<MeasurementDAOResponse> resp = new ArrayList<>();
		
		int i = 0;
		for(FluxTable table: tables) {
			MeasurementDAOResponse measurement;
			for(FluxRecord fluxRecord : table.getRecords()) {
				if(resp.size() <= i) {
					// Initialize measurement and add it to map
					measurement = new MeasurementDAOResponse();
					measurement.setMeasurement(fluxRecord.getMeasurement());
					resp.add(measurement); 
					
					// Set time field
					Entry<String, String> field = FieldsFormat.parseSeries("time", fluxRecord.getTime());
					measurement.getFields().put(field.getKey(), field.getValue());
				} else measurement = resp.get(i);
				// Add other fields
				if (fluxRecord.getField() != null) {
					Entry<String, String> field = FieldsFormat.parseSeries(fluxRecord.getField(), fluxRecord.getValue());
					measurement.getFields().put(field.getKey(), field.getValue());
				}
				// Add tags
				setTags(measurement, fluxRecord);
				i++;
			}
		}
		
		return new ArrayList<>(resp);
	}

	private static void setTags(MeasurementDAOResponse measurement, FluxRecord fluxRecord) {
		if (measurement.getTags() == null) measurement.setTags(new HashMap<>());
		for(String key : fluxRecord.getValues().keySet()) {
			if (key != null && !(key.startsWith("_") || key.equalsIgnoreCase("table") || key.equalsIgnoreCase("result"))) {
				String value = fluxRecord.getValueByKey(key) != null? fluxRecord.getValueByKey(key).toString() : null;
				measurement.getTags().put(key, value);
			}
		}		
	}
}
