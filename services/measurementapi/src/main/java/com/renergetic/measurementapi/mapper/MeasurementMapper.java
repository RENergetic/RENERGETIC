
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
	public static List<MeasurementDAOResponse> fromFlux(List<FluxTable> tables) {
		ArrayList<MeasurementDAOResponse> resp = new ArrayList<>();
		
		int i = 0;
		for(FluxTable table: tables) {
			MeasurementDAOResponse measurement;
			for(FluxRecord record : table.getRecords()) {
				if(resp.size() <= i) {
					// Initialize measurement and add it to map
					measurement = new MeasurementDAOResponse();
					measurement.setFields(new HashMap<>());
					measurement.setTags(new HashMap<>());
					measurement.setMeasurement(record.getMeasurement());
					resp.add(measurement); 
					
					// Set time field
					Entry<String, String> field = FieldsFormat.parseSeries("time", record.getTime());
					measurement.getFields().put(field.getKey(), field.getValue());
				} else measurement = resp.get(i);
				// Add other fields
				if (record.getField() != null) {
					Entry<String, String> field = FieldsFormat.parseSeries(record.getField(), record.getValue());
					measurement.getFields().put(field.getKey(), field.getValue());
				}
				// Add tags
				if (measurement.getTags() == null) measurement.setTags(new HashMap<String, String>());
				for(String key : record.getValues().keySet()) {
					if (key != null && !(key.startsWith("_") || key.equalsIgnoreCase("table") || key.equalsIgnoreCase("result")))
						measurement.getTags().put(key, record.getValueByKey(key).toString());
				}
				i++;
			}
		}
		
		return new ArrayList<>(resp);
	}
}
