package com.renergetic.measurementapi.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.renergetic.common.model.InfluxFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.DeletePredicateRequest;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;
import com.renergetic.measurementapi.dao.MeasurementDAORequest;
import com.renergetic.measurementapi.dao.MeasurementDAOResponse;
import com.renergetic.measurementapi.exception.InvalidArgumentException;
import com.renergetic.measurementapi.mapper.MeasurementMapper;
import com.renergetic.measurementapi.model.InfluxTimeUnit;
import com.renergetic.measurementapi.service.utils.FieldsFormat;
import com.renergetic.measurementapi.service.utils.ManageTags;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MeasurementService {
	@Autowired
    private InfluxDBClient influxDB;

	@Autowired
	private ManageTags manageTags;

	public void insert(MeasurementDAORequest measurement) {
		WriteApiBlocking write = influxDB.getWriteApiBlocking();
		
		Point registry = Point.measurement(measurement.getMeasurement());
		if (measurement.getFields().containsKey("time")) {
			try {
				String timeField = measurement.getFields().get("time");
				Instant time = null;
				if (!timeField.matches("^\\d+$"))
					time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeField).toInstant();
				
				registry.time(time != null? time.toEpochMilli() : Long.parseLong(timeField), WritePrecision.MS);
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

	public List<MeasurementDAOResponse> data(String bucket, List<String> measurements, List<String> fields, Map<String, List<String>> tags, String from, String to, String timeVar, Boolean performDecumulation, Boolean getTags, Boolean onlyLatestPrediction) {
		QueryApi query = influxDB.getQueryApi();

		List<String> fluxQuery = new ArrayList<>();

		if (timeVar.equals("time")) {
			from = parseTime(from);
			to = parseTime(to);
		}
		// ADD REQUEST BUCKET AND TIME RANGE
		fluxQuery.add(String.format("from(bucket: \"%s\")", bucket));
		fluxQuery.add(String.format("range(start: %s, stop: %s)", !from.isEmpty()? from : "0", !to.isEmpty()? to : "now()"));
		

		// FILTER MEASUREMENTS IN MEASUREMENTS LIST IF LIST IS EMPTY SEARCH IN ALL MEASUREMENTS
		if (measurements != null && !measurements.isEmpty())
			fluxQuery.add( String.format("filter(fn: (r) => %s)",
					measurements.stream().map(measurement -> String.format("r[\"_measurement\"] == \"%s\"", measurement)).collect(Collectors.joining(" or "))) );

		// FILTER FIELDS IN FIELDS LIST IF LIST IS EMPTY SEARCH IN ALL FIELDS
		if (fields != null && !fields.isEmpty())
			fluxQuery.add( String.format("filter(fn: (r) => %s)",
					fields.stream().map(field -> String.format("r[\"_field\"] == \"%s\"", field)).collect(Collectors.joining(" or "))) );

		// IF ONLY FETCHING LATEST PREDICITON GENERATION
		if (Boolean.TRUE.equals(onlyLatestPrediction)){
			List<String> predictionValues = listTagValues(bucket, "time_prediction", measurements, fields, tags, from, to);
			if(predictionValues != null && !predictionValues.isEmpty()){
				String latest = null;
				for(String predTime : predictionValues){
					if(latest == null || latest.compareTo(predTime) < 0)
						latest = predTime;
				}
				if(tags == null)
					tags = new HashMap<>();
				tags.put("time_prediction", List.of(latest));
			}
		}

		// FILTER TAGS AND VALUES RELATED BY THEM, IF A ENTRY HAVEN'T A TAG IS DISCARDED, IF THE LIST IS EMPTY IGNORE THE TAGS
		if (tags != null && !tags.isEmpty()) {
			fluxQuery.add( manageTags.fluxFilter(tags) );
		}
		
		// IF DATA IS CUMULATIVE CONVERT TO NON CUMULATIVE DATA
		if (Boolean.TRUE.equals(performDecumulation))
			fluxQuery.add("difference(columns: [\"_value\"])");

		String flux = fluxQuery.stream().collect(Collectors.joining(" |> "));

		log.info("Flux query: \n" + flux);
		List<FluxTable> tables = query.query(flux);
		return MeasurementMapper.fromFlux(tables, getTags);
	}

	public List<MeasurementDAOResponse> dataOperation(String bucket, InfluxFunction function, List<String> measurements, List<String> fields, Map<String, List<String>> tags, String from, String to, String timeVar, String group, Boolean byMeasurement, Boolean toFloat, Boolean performDecumulation, Boolean getTags, Boolean onlyLatestPrediction) {
		QueryApi query = influxDB.getQueryApi();

		List<String> fluxQuery = new ArrayList<>();

		if (timeVar.equals("time")) {
			from = parseTime(from);
			to = parseTime(to);
		}
		
		if (group != null && !group.isBlank()) {
			group = InfluxTimeUnit.convert(group, InfluxTimeUnit.ms);
		} else group = null;
		// ADD REQUEST BUCKET AND TIME RANGE
		fluxQuery.add(String.format("from(bucket: \"%s\")", bucket));
		fluxQuery.add(String.format("range(start: %s, stop: %s)", !from.isEmpty()? from : "0", !to.isEmpty()? to : "now()"));
		
		// FILTER MEASUREMENTS IN MEASUREMENTS LIST IF LIST IS EMPTY SEARCH IN ALL MEASUREMENTS
				if (measurements != null && !measurements.isEmpty())
					fluxQuery.add( String.format("filter(fn: (r) => %s)",
							measurements.stream().map(measurement -> String.format("r[\"_measurement\"] == \"%s\"", measurement)).collect(Collectors.joining(" or "))) );

		// FILTER FIELDS IN FIELDS LIST IF LIST IS EMPTY SEARCH IN ALL FIELDS
		if (fields != null && !fields.isEmpty())
			fluxQuery.add( String.format("filter(fn: (r) => %s)",
					fields.stream().map(field -> String.format("r[\"_field\"] == \"%s\"", field)).collect(Collectors.joining(" or "))) );

		// IF ONLY FETCHING LATEST PREDICITON GENERATION
		if (Boolean.TRUE.equals(onlyLatestPrediction)){
			List<String> predictionValues = listTagValues(bucket, "time_prediction", measurements, fields, tags, from, to);
			if(predictionValues != null && !predictionValues.isEmpty()){
				String latest = null;
				for(String predTime : predictionValues){
					if(latest == null || latest.compareTo(predTime) < 0)
						latest = predTime;
				}
				if(tags == null)
					tags = new HashMap<>();
				tags.put("time_prediction", List.of(latest));
			}
		}

		// FILTER TAGS AND VALUES RELATED BY THEM, IF A ENTRY HAVEN'T A TAG IS DISCARDED, IF THE LIST IS EMPTY IGNORE THE TAGS
		if (tags != null && !tags.isEmpty()) {
			fluxQuery.add( manageTags.fluxFilter(tags) );
		}
		fluxQuery.add("filter(fn: (r) => types.isType(v: r._value, type: \"float\") or types.isType(v: r._value, type: \"int\"))");

		// IF DATA IS CUMULATIVE CONVERT TO NON CUMULATIVE DATA
		if (Boolean.TRUE.equals(performDecumulation))
			fluxQuery.add("difference(columns: [\"_value\"])");
		
		// GROUP DATA
		if (Boolean.TRUE.equals(byMeasurement)) 
			fluxQuery.add("group(columns: [\"_measurement\"])");
		else fluxQuery.add("group()");
		if (group != null)
			fluxQuery.add(String.format("window(every: %1$s, period: %1$s, startColumn: \"_time\", stopColumn: \"_stop\", timeColumn: \"_time\")", group));
		if (Boolean.TRUE.equals(toFloat)) fluxQuery.add("toFloat()");
		
		// OPERATE DATA AND ADD FIELD NAME
		fluxQuery.add(String.format("%s(column: \"_value\")", function.name().toLowerCase()));
		fluxQuery.add("group()");
		fluxQuery.add(String.format("set(key: \"_field\", value: \"%s\")", function.name().toLowerCase()));
		
		String flux = "import \"types\"" + fluxQuery.stream().collect(Collectors.joining(" |> "));

		log.info("Flux query: \n" + flux);
		List<FluxTable> tables = query.query(flux);
		return MeasurementMapper.fromFlux(tables, getTags);
	}

	/**
     * List all tags names and its values
     * @param bucket Bucket from search measurement
     * @return A Map with all tags names in Bucket
     */
    public Map<String, List<String>> listTags(String bucket, List<String> measurements, List<String> fields, Map<String, List<String>> tags, String from, String to) {
		QueryApi query = influxDB.getQueryApi();

		List<String> filter = new ArrayList<>();

		if (from != null) from = parseTime(from);
		if (to != null) to = parseTime(to);

		// FILTER USING MEASUREMENTS
		if (measurements != null && !measurements.isEmpty())
			filter.add( '(' + 
					measurements.stream().map(measurement -> String.format("r[\"_measurement\"] == \"%s\"", measurement)).collect(Collectors.joining(" or ")) + ')' );

		// FILTER USING FIELD
		if (fields != null && !fields.isEmpty())
			filter.add( '(' + 
					fields.stream().map(field -> String.format("r[\"_field\"] == \"%s\"", field)).collect(Collectors.joining(" or ")) + ')');

		// FILTER USING TAGS AND ITS VALUES
		if (tags != null && !tags.isEmpty())
			filter.add( '(' + 
					tags.keySet().stream()
					.map(key -> '(' + tags.get(key).stream().map(value -> String.format("r[\"%s\"] == \"%s\"", key, value)).collect(Collectors.joining(" or ")) + ')' )
					.collect(Collectors.joining(" and ")) + ')' );
		
		String filterString = filter.stream().collect(Collectors.joining(" and "));
		
		String flux = String.format("import \"influxdata/influxdb/v1\""
				+ "v1.tagKeys(bucket: \"%s\"%s%s%s)",
				bucket, filterString.isBlank()? "" : ", predicate: (r) => " + filterString,
				from == null? "" : ", start: " + from,
				to == null? "": ", stop: " + to);

		String fromVar = from;
		String toVar = to;

		log.info("Flux query: \n" + flux);
		FluxTable table = query.query(flux).get(0);
		return table.getRecords()
			.stream()
			.map(row -> row.getValue().toString())
			.collect(Collectors.toMap(tagKey -> tagKey, 
					tagKey -> listTagValues(bucket, tagKey, filterString, fromVar, toVar)));
    }

    public List<String> listTagValues(String bucket, String tag, List<String> measurements, List<String> fields, Map<String, List<String>> tags, String from, String to) {
		QueryApi query = influxDB.getQueryApi();

		List<String> filter = new ArrayList<>();

		if (from != null) from = parseTime(from);
		if (to != null) to = parseTime(to);

		// FILTER USING MEASUREMENTS
		if (measurements != null && !measurements.isEmpty())
			filter.add( '(' + 
					measurements.stream().map(measurement -> String.format("r[\"_measurement\"] == \"%s\"", measurement)).collect(Collectors.joining(" or ")) + ')' );

		// FILTER USING FIELD
		if (fields != null && !fields.isEmpty())
			filter.add( '(' +
					fields.stream().map(field -> String.format("r[\"_field\"] == \"%s\"", field)).collect(Collectors.joining(" or ")) + ')');

		// FILTER USING TAGS AND ITS VALUES
		if (tags != null && !tags.isEmpty())
			filter.add( '(' + 
					tags.keySet().stream()
					.map(key -> '(' + tags.get(key).stream().map(value -> String.format("r[\"%s\"] == \"%s\"", key, value)).collect(Collectors.joining(" or ")) + ')' )
					.collect(Collectors.joining(" and ")) + ')' );
		
		String filterString = filter.stream().collect(Collectors.joining(" and "));
		
		String flux = String.format("import \"influxdata/influxdb/v1\""
				+ "v1.tagValues(bucket: \"%s\", tag: \"%s\"%s%s%s)",
				bucket, tag, filterString.isBlank()? "" : ", predicate: (r) => " + filterString,
				from == null? "" : ", start: " + from,
				to == null? "": ", stop: " + to);

		log.info("Flux query: \n" + flux);
		FluxTable table = query.query(flux).get(0);
		return table.getRecords()
			.stream()
			.map(row -> row.getValue().toString())
			.collect(Collectors.toList());
    }

    public List<String> listTagValues(String bucket, String tag, String filter, String from, String to) {
		QueryApi query = influxDB.getQueryApi();
		
		String flux = String.format("import \"influxdata/influxdb/v1\""
				+ "v1.tagValues(bucket: \"%s\", tag: \"%s\"%s%s%s)",
				bucket, tag, filter.isBlank()? "" : ", predicate: (r) => " + filter,
				from == null? "" : ", start: " + from,
				to == null? "": ", stop: " + to);

		log.info("Flux query: \n" + flux);
		FluxTable table = query.query(flux).get(0);
		return table.getRecords()
			.stream()
			.map(row -> row.getValue().toString())
			.collect(Collectors.toList());
    }
    
	/**
     * List all measurement names
     * @param bucket Bucket from search measurement
     * @return A list with all measurement names in Bucket
     */
    public List<String> list(String bucket) {
		QueryApi query = influxDB.getQueryApi();
		
		String flux = String.format("import \"influxdata/influxdb/schema\""
				+ "schema.measurements(bucket: \"%s\", start: -30y)",
				bucket);

		log.info("Flux query: \n" + flux);
		List<FluxTable> tables = query.query(flux);
		return tables.get(0).getRecords()
				.stream().map(row -> row.getValue().toString()).collect(Collectors.toList());
    }

	public void delete(MeasurementDAORequest measurement, String from, String to) {
		try {
			DeleteApi delete = influxDB.getDeleteApi();
			DeletePredicateRequest request = new DeletePredicateRequest();
			request.setPredicate("_measurement = " + measurement.getMeasurement());
			
			if (!from.isBlank())
				request.setStart(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(from).toInstant()
					  .atOffset(ZoneOffset.UTC));
			else request.setStart(Instant.ofEpochMilli(0).atOffset(ZoneOffset.UTC));
			if (!to.isBlank())
				request.setStop(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(to).toInstant()
					  .atOffset(ZoneOffset.UTC));
			else request.setStop(Instant.now().plus(36500, ChronoUnit.DAYS).atOffset(ZoneOffset.UTC));
			
			delete.delete(request, measurement.getBucket(), "renergetic");
		} catch(ParseException e) {
			log.error("Date from or to dates haven't a valid format");
		}
	}

	@Deprecated
	public List<MeasurementDAOResponse> select(MeasurementDAORequest measurement, String from, String to, String timeVar) {
		QueryApi query = influxDB.getQueryApi();

		String where = null;
		if (!measurement.getTags().isEmpty())
			where = " |> filter(fn: (r) => " + String.join(" and ", measurement.getTags().keySet().stream().map(key -> String.format("r[\"%s\"] == \"%s\"", key, measurement.getTags().get(key))).collect(Collectors.toList())) + ')';
		
		if (timeVar.equals("time")) {
			from = from.isEmpty()? "0" : from;
			to = to.isEmpty()? "now()" : to;
		}
		
		String flux = String.format(
				"from(bucket: \"%s\") |> "
				+ "range(start: %s, stop: %s) |> "
				+ "filter(fn: (r) => r[\"_measurement\"] == \"%s\")%s",
				measurement.getBucket(),
				!from.isEmpty()? from : "0",
				!to.isEmpty()? to : "now()",
				measurement.getMeasurement(),
				where != null? where : "");

		log.info("Flux query: \n" + flux);
		List<FluxTable> tables = query.query(flux);
		return MeasurementMapper.fromFlux(tables);
	}
	
	/**
     * Group the power data and calculate the indicated operation
     * @param function operation to execute with Database data | See all ops in InfluxFunction enum
     * @param from Date of first power data to get | Format: yyyy-MM-dd hh:mm:ss
     * @param to Date of last power data to get | Format: yyyy-MM-dd hh:mm:ss
     * @param group Time to group the data (example: 1596039s)
     * @return The sum of power data group by time
     */
	@Deprecated
    public List<MeasurementDAOResponse> operate(MeasurementDAORequest measurement, InfluxFunction function, String field, String from, String to, String group, String timeVar) {
		QueryApi query = influxDB.getQueryApi();

		String where = null;
		if (!measurement.getTags().isEmpty())
			where = "filter(fn: (r) => " + String.join(" and ", measurement.getTags().keySet().stream().map(key -> String.format("r[\"%s\"] == \"%s\"", key, measurement.getTags().get(key))).collect(Collectors.toList())) + ") |> ";

		if (timeVar.equals("time")) {
			from = parseTime(from);
			to = parseTime(to);
		}
		
		if (group != null && !group.isBlank()) {
			group = InfluxTimeUnit.convert(group, InfluxTimeUnit.ms);
			group = "window(every: " + group + ", startColumn: \"_time\") |> ";
		} else group = null;
		
		String flux = String.format(
				"from(bucket: \"%s\") |> "
				+ "range(start: %s, stop: %s) |> "
				+ "filter(fn: (r) => r[\"_measurement\"] == \"%s\" and r[\"_field\"] == \"%s\") |> %s"
				+ "group(columns:[\"_measurement\"], mode:\"by\") |> %s"
				+ "%s(column: \"_value\") |> "
				+ "set(key: \"_field\", value: \"%s\")",
				measurement.getBucket(),
				!from.isEmpty()? from : "0",
				!to.isEmpty()? to : "now()",
				measurement.getMeasurement(),
				field,
				where != null? where : "",
				group != null? group : "",
				function.name().toLowerCase(),
				function.name().toLowerCase());

		log.info("Flux query: \n" + flux);
		List<FluxTable> tables = query.query(flux);
		return MeasurementMapper.fromFlux(tables);
    }
    
	private String parseTime(String time) {
		if (!time.isEmpty() && !time.matches("^\\d+$")) {
			if (time.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*")) {
				time = time.replace(" ", "T")+'Z';
			} else time = "-" + InfluxTimeUnit.convert(time, InfluxTimeUnit.ms);
		} else if (!time.isEmpty()) time = String.valueOf(Long.parseLong(time)/1000);

		return time;
	}
}
