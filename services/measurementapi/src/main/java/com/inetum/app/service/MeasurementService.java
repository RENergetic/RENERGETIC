package com.inetum.app.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inetum.app.dao.MeasurementDAORequest;
import com.inetum.app.dao.MeasurementDAOResponse;
import com.inetum.app.exception.InvalidArgumentException;
import com.inetum.app.mapper.MeasurementMapper;
import com.inetum.app.model.InfluxFunction;
//import com.inetum.app.mapper.MeasurementMapper;
import com.inetum.app.model.InfluxTimeUnit;
import com.inetum.app.service.utils.FieldsFormat;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;

@Service
public class MeasurementService {
	@Autowired
    private InfluxDBClient influxDB;

	public void insert(MeasurementDAORequest measurement) {
		WriteApiBlocking write = influxDB.getWriteApiBlocking();
		
		Point registry = Point.measurement(measurement.getMeasurement());
		if (measurement.getFields().containsKey("time")) {
			try {
				Instant time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(measurement.getFields().get("time")).toInstant();
				registry.time(time.toEpochMilli(), WritePrecision.MS);
			} catch(ParseException error) {
				error.printStackTrace();
				throw new InvalidArgumentException("Invalid value to time field");
			}
		} else
			registry.time(System.currentTimeMillis(), WritePrecision.MS);
    	Entry<String, ?> field = null;
    	
    	for (Entry<String, String> fieldString : measurement.getFields().entrySet()) {
    		field = FieldsFormat.parseField(fieldString);
    		
    		if (field.getValue() instanceof String)
    			registry.addField(field.getKey(), (String) field.getValue());
    		else if (field.getValue() instanceof Double)
    			registry.addField(field.getKey(), (Double) field.getValue());
    		else if (field.getValue() instanceof Long)
    			registry.addField(field.getKey(), (Long) field.getValue());
    		else if (field.getValue() instanceof Boolean)
    			registry.addField(field.getKey(), (Boolean) field.getValue());
    	}
    	
		registry.addTags(measurement.getTags());    	

    	write.writePoint(measurement.getBucket() == null || measurement.getBucket().isBlank()
    			? "renergetic" : measurement.getBucket(), 
    			"renergetic", 
    			registry);
	}

	public List<MeasurementDAOResponse> select(MeasurementDAORequest measurement, String from, String to, String timeVar) {
		QueryApi query = influxDB.getQueryApi();

		Long fromN = 0L;
		Long toN = 0L;
		String where = null;
		if (!measurement.getTags().isEmpty())
			where = " |> filter(fn: (r) => " + String.join(" and ", measurement.getTags().keySet().stream().map(key -> String.format("r[\"%s\"] == \"%s\"", key, measurement.getTags().get(key))).collect(Collectors.toList())) + ")";
		
		if (timeVar.equals("time")) {
			if (from.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
				from = '\''+from.replace(" ", "T")+'Z'+'\'';
			else fromN = InfluxTimeUnit.convertNumber(from, InfluxTimeUnit.ms);
			
			if (to.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
				to = '\''+to.replace(" ", "T")+'Z'+'\'';
			else toN = InfluxTimeUnit.convertNumber(to, InfluxTimeUnit.ms);	
		}
		
		String flux = String.format(
				"from(bucket: \"%s\") |> "
				+ "range(start: %d, stop: %s) |> "
				+ "filter(fn: (r) => r[\"_measurement\"] == \"%s\")%s",
				measurement.getBucket(),
				fromN,
				toN != 0? toN : "now()",
				measurement.getMeasurement(),
				where != null? where : "");

		System.err.println(flux);
		List<FluxTable> tables = query.query(flux);
		return MeasurementMapper.fromFlux(tables);
        //QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
 
        //return MeasurementMapper.fromSeries(queryResult.getResults().get(0).getSeries().get(0));
	}
	
	/**
     * Group the power data and calculate the indicated operation
     * @param function operation to execute with Database data | See all ops in InfluxFunction enum
     * @param from Date of first power data to get | Format: yyyy-MM-dd hh:mm:ss
     * @param to Date of last power data to get | Format: yyyy-MM-dd hh:mm:ss
     * @param group Time to group the data (example: 1596039s)
     * @return The sum of power data group by time
     */
    public List<MeasurementDAOResponse> operate(MeasurementDAORequest measurement, InfluxFunction function, String field, String from, String to, String group, String timeVar) {
		QueryApi query = influxDB.getQueryApi();

		Long fromN = 0L;
		Long toN = 0L;
		String where = null;
		if (!measurement.getTags().isEmpty())
			where = "filter(fn: (r) => " + String.join(" and ", measurement.getTags().keySet().stream().map(key -> String.format("r[\"%s\"] == \"%s\"", key, measurement.getTags().get(key))).collect(Collectors.toList())) + ") |> ";
		
		if (timeVar.equals("time")) {
			if (from.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
				from = '\''+from.replace(" ", "T")+'Z'+'\'';
			else fromN = InfluxTimeUnit.convertNumber(from, InfluxTimeUnit.ms);
			
			if (to.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
				to = '\''+to.replace(" ", "T")+'Z'+'\'';
			else toN = InfluxTimeUnit.convertNumber(to, InfluxTimeUnit.ms);	
		}
		if (group != null && !group.isBlank()) {
			group = InfluxTimeUnit.convert(group, InfluxTimeUnit.ms);
			group = "window(every: " + group + ", startColumn: \"_time\") |> ";
		} else group = null;
		
		String flux = String.format(
				"from(bucket: \"%s\") |> "
				+ "range(start: %d, stop: %s) |> "
				+ "filter(fn: (r) => r[\"_measurement\"] == \"%s\" and r[\"_field\"] == \"%s\") |> %s"
				+ "group(columns:[\"_measurement\"], mode:\"by\") |> %s"
				+ "%s(column: \"_value\") |> "
				+ "set(key: \"_field\", value: \"%s\")",
				measurement.getBucket(),
				fromN,
				toN != 0? toN : "now()",
				measurement.getMeasurement(),
				field,
				where != null? where : "",
				group != null? group : "",
				function.name().toLowerCase(),
				function.name().toLowerCase());

		System.err.println(flux);
		List<FluxTable> tables = query.query(flux);
		return MeasurementMapper.fromFlux(tables);
    }
	
	/**
     * List all measurement names
     * @param bucket Bucket from search measurement
     * @return A list with all measurement names in Bucket
     */
    public List<String> list(String bucket) {
		QueryApi query = influxDB.getQueryApi();
		
		String flux = String.format("from(bucket: \"%s\")"
				+ " |> range(start: -30y)"
				+ " |> drop(fn: (column) => column != \"_measurement\")"
				+ " |> distinct(column: \"_measurement\")",
				bucket);

		System.err.println(flux);
		List<FluxTable> tables = query.query(flux);
		return MeasurementMapper.fromFlux(tables)
				.stream().map(measurement -> measurement.getMeasurement()).collect(Collectors.toList());
    }
    
}
