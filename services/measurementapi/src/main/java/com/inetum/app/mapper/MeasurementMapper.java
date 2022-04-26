package com.inetum.app.mapper;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.influxdb.dto.QueryResult.Series;

import com.inetum.app.dao.MeasurementDAOResponse;
import com.inetum.app.service.utils.FieldsFormat;

public abstract class MeasurementMapper {
	public static List<MeasurementDAOResponse> fromSeries(Series result) {
		MeasurementDAOResponse[] resp = new MeasurementDAOResponse[result.getValues().size()];
		
		for(int i = 0; i < result.getColumns().size(); i++) {
			for (int j = 0; j < result.getValues().size(); j++) {
				if (resp[j] == null) {
					resp[j] = new MeasurementDAOResponse();
					resp[j].setMeasurement(result.getName());
					resp[j].setFields(new HashMap<>());
				}
				Entry<String, String> field = FieldsFormat.parseSeries(new SimpleEntry<>(result.getColumns().get(i), result.getValues().get(j).get(i)));
				resp[j].getFields().put(field.getKey(), field.getValue());
			}
		}
		
		return Arrays.asList(resp);
	}
}
