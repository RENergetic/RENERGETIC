
package com.inetum.app.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.inetum.app.dao.MeasurementDAOResponse;
import com.inetum.app.service.utils.FieldsFormat;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

public abstract class MeasurementMapper {
	public static List<MeasurementDAOResponse> fromFlux(List<FluxTable> tables) {
		Map<Instant, MeasurementDAOResponse> resp = new LinkedHashMap<>();
		
		for(FluxTable table: tables) {
			MeasurementDAOResponse measurement;
			for(FluxRecord record : table.getRecords()) {
				if(!resp.containsKey(record.getTime())) {
					// Initialize measurement and add it to map
					measurement = new MeasurementDAOResponse();
					measurement.setFields(new HashMap<>());
					measurement.setTags(new HashMap<>());
					measurement.setMeasurement(record.getMeasurement());
					resp.put(record.getTime(), measurement); 
					
					// Set time field
					Entry<String, String> field = FieldsFormat.parseSeries("time", record.getTime());
					measurement.getFields().put(field.getKey(), field.getValue());
				} else measurement = resp.get(record.getTime());
				// Add other fields
				if (record.getField() != null) {
					Entry<String, String> field = FieldsFormat.parseSeries(record.getField(), record.getValue());
					measurement.getFields().put(field.getKey(), field.getValue());
				}
				// Add tags
				for(String key : record.getValues().keySet()) {
					if (key != null && !(key.startsWith("_") || key.equalsIgnoreCase("table") || key.equalsIgnoreCase("result")))
						measurement.getTags().put(key, record.getValueByKey(key).toString());
				}
			}
		}
		
		return new ArrayList<>(resp.values());
	}
}
