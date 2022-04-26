package com.inetum.app.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inetum.app.dao.MeasurementDAORequest;
import com.inetum.app.dao.MeasurementDAOResponse;
import com.inetum.app.mapper.MeasurementMapper;
import com.inetum.app.model.InfluxFunction;
import com.inetum.app.model.InfluxTimeUnit;
import com.inetum.app.service.utils.FieldsFormat;

@Service
public class MeasurementService {
	@Autowired
    private InfluxDB influxDB;

	public void insert(MeasurementDAORequest measurement) {
    	Builder registry = Point.measurement(measurement.getMeasurement());
    	registry.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
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
    	
    	for (Entry<String, String> tag : measurement.getTags().entrySet())
    		registry.addField(tag.getKey(), tag.getValue());    	

        influxDB.write(measurement.getBucket(), "autogen", registry.build());
	}

	public List<MeasurementDAOResponse> select(MeasurementDAORequest measurement, String from, String to, String timeVar) {
		String where = String.join(" AND ", measurement.getTags().keySet().stream().map(key -> String.format("\"%s\"='%s'", key, measurement.getTags().get(key))).collect(Collectors.toList()));
		
		if (timeVar.equals("time")) {
			if (from.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
				from = '\''+from.replace(" ", "T")+'Z'+'\'';
			else from = InfluxTimeUnit.convert(from, InfluxTimeUnit.ms);
			
			if (to.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
				to = '\''+to.replace(" ", "T")+'Z'+'\'';
			else to = InfluxTimeUnit.convert(to, InfluxTimeUnit.ms);
			
			if (!measurement.getTags().isEmpty())
				where = String.format("%s >= %s%s", timeVar, from, !to.equals("0ms")? " AND " + timeVar + " <= " + to : "") + " AND " + where;
			else where = String.format("%s >= %s%s", timeVar, from, !to.equals("0ms")? " AND " + timeVar + " <= " + to : "");
		} else {
			Long fromNum = 0L;
			Long toNum = 0L;
			try {
				if (from.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
					fromNum = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(from).toInstant().toEpochMilli();
				else fromNum = InfluxTimeUnit.convertNumber(from, InfluxTimeUnit.ms);
				if (to.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
					toNum = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(to).toInstant().toEpochMilli();
				else toNum = InfluxTimeUnit.convertNumber(to, InfluxTimeUnit.ms);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!measurement.getTags().isEmpty())
				where = String.format("%s >= %d%s", timeVar, fromNum, !(toNum == 0)? " AND " + timeVar + " <= " + toNum : "") + " AND " + where;
			else where = String.format("%s >= %d%s", timeVar, fromNum, !(toNum == 0)? " AND " + timeVar + " <= " + toNum : "");
		}
		System.err.printf("SELECT * FROM %s%s\n", 
				measurement.getMeasurement(),
				!where.isBlank()? " WHERE " + where : "");
		
		Query query = new Query(String.format("SELECT * FROM %s%s", 
				measurement.getMeasurement(),
				!where.isBlank()? " WHERE " + where : ""), 
				measurement.getBucket());
		
        QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
 
        return MeasurementMapper.fromSeries(queryResult.getResults().get(0).getSeries().get(0));
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
    	String where = String.join(" AND ", measurement.getTags().keySet().stream().map(key -> String.format("\"%s\"='%s'", key, measurement.getTags().get(key))).collect(Collectors.toList()));

		if (timeVar.equals("time")) {
			if (from.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
				from = '\''+from.replace(" ", "T")+'Z'+'\'';
			else from = InfluxTimeUnit.convert(from, InfluxTimeUnit.ms);
			
			if (to.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
				to = '\''+to.replace(" ", "T")+'Z'+'\'';
			else to = InfluxTimeUnit.convert(to, InfluxTimeUnit.ms);
			
			if (!measurement.getTags().isEmpty())
				where = String.format("%s >= %s%s", timeVar, from, !to.equals("0ms")? " AND " + timeVar + " <= " + to : "") + " AND " + where;
			else where = String.format("%s >= %s%s", timeVar, from, !to.equals("0ms")? " AND " + timeVar + " <= " + to : "");
		} else {
			Long fromNum = 0L;
			Long toNum = 0L;
			try {
				if (from.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
					fromNum = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(from).toInstant().toEpochMilli();
				else fromNum = InfluxTimeUnit.convertNumber(from, InfluxTimeUnit.ms);
				if (to.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
					toNum = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(to).toInstant().toEpochMilli();
				else toNum = InfluxTimeUnit.convertNumber(to, InfluxTimeUnit.ms);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!measurement.getTags().isEmpty())
				where = String.format("%s >= %d%s", timeVar, fromNum, !(toNum == 0)? " AND " + timeVar + " <= " + toNum : "") + " AND " + where;
			else where = String.format("%s >= %d%s", timeVar, fromNum, !(toNum == 0)? " AND " + timeVar + " <= " + toNum : "");
		}
		group = InfluxTimeUnit.convert(group, InfluxTimeUnit.ms);
		
		System.err.printf("SELECT %s(%s) FROM %s%s%s\n", 
				function.name(),
				field,
				measurement.getMeasurement(),				
				!where.isBlank()? " WHERE " + where : "",
				!group.equals("0ms")? " GROUP BY time(" + group + ") fill(linear)" : "");
		
		Query query = new Query(String.format("SELECT %s(%s) FROM %s%s%s", 
				function.name(),
				field,
				measurement.getMeasurement(),
				!where.isBlank()? " WHERE " + where : "",
				!group.equals("0ms")? " GROUP BY time(" + group + ") fill(linear)" : ""), 
				measurement.getBucket());
		
        QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
 
        return MeasurementMapper.fromSeries(queryResult.getResults().get(0).getSeries().get(0));
    }
}
