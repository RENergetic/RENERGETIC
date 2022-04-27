package com.inetum.app.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;
import org.influxdb.InfluxDBMapperException;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.inetum.app.model.GeneralMeasurement;

@Repository
public class GeneralRepository {
	@Autowired
    private InfluxDB influxDB;

	public final String DATABASE = "renergetic";
	public final String MEASUREMENT_NAME = "heat_supply";

	/**
	 * Insert a power data to myMeasurement table
	 * @param power Power to set
	 */
    public void insert(GeneralMeasurement measurement) {
        Builder registry = Point.measurement(MEASUREMENT_NAME)
        		.tag(measurement.getTags())
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        
    	for (String field : measurement.getFields().keySet()) {
    		Object value = measurement.getFields().get(field);
    		if (value instanceof String)
    			registry.addField(field, (String) value);
    		else if (value instanceof Number)
    			registry.addField(field, (Number) value);
    	}
    	
        influxDB.write(DATABASE, "autogen", registry.build());
    }
    
    /**
     * Get all data from database
     * @return All power data from database as List
     */
    public List<QueryResult.Result> select(String measurementName, Map<String, String> tags) {
		String pTags = String.join(" AND ", tags.keySet().stream().map(key -> String.format("\"%s\"='%s'", key, tags.get(key))).collect(Collectors.toList()));
		
		Query query = new Query("SELECT * FROM " + measurementName +
				((tags != null && tags.size() > 0)? " WHERE " + pTags : ""), DATABASE);
		
        QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
 
        return queryResult.getResults();
        //SELECT sum("power") FROM "myMeasurement" WHERE time >= now() - 30d and time <= now() GROUP BY time(1h) fill(null)
    }
    
    /**
     * Allows to execute any Query to any table in DATABASE
     * @param queryArg Query to execute, it must is in InfluxQL format
     * Only Select and Show queries are accepted
     * @return The influxDB return data as List
     */
    public List<QueryResult.Result> query(String queryArg) {
		List<QueryResult.Result> results = new ArrayList<QueryResult.Result>();
		if (queryArg.toLowerCase().startsWith("select") || queryArg.toLowerCase().startsWith("show"))
			try {
				Query query = new Query(queryArg, DATABASE);
				
				// EXECUTE QUERY AND GET THE RESULTS AS INFLUX FORMAT
		        QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
		        results = queryResult.getResults();
		        
	 		} catch(InfluxDBException | InfluxDBMapperException e) {
	 			e.printStackTrace();
	 			results = null;
	 		}
		else results = null;
 
        return results;
    }
}
