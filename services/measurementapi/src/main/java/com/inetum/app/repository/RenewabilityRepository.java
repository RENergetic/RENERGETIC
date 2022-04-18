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
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.inetum.app.model.InfluxFunction;
import com.inetum.app.model.InfluxTimeUnit;
import com.inetum.app.model.Renewability;

@Repository
public class RenewabilityRepository {
	@Autowired
    private InfluxDB influxDB;

	public final String DATABASE = "renergetic";

	/**
	 * Insert a renewability data to myMeasurement table
	 * @param renewability renewability to set
	 */
    public void insert(Renewability renewability, Map<String, String> tags) {
        Builder registry = Point.measurementByPOJO(renewability.getClass())
        		.addFieldsFromPOJO(renewability)
        		.addField("time_prediction", renewability.getTimePred().toEpochMilli())
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        if (tags != null && !tags.isEmpty())
        	registry.tag(tags);

        influxDB.write(DATABASE, "autogen", registry.build());
    }
    
    /**
     * Get all renewability data from database
     * @return All renewability data from database as List
     */
    public List<Renewability> select(Map<String, String> tags) {
		List<Renewability> results = new ArrayList<Renewability>();
		String pTags = String.join(" AND ", tags.keySet().stream().map(key -> String.format("\"%s\"='%s'", key, tags.get(key))).collect(Collectors.toList()));
		pTags = '(' + pTags + ')';
		
		Query query = new Query("SELECT * FROM " + Renewability.measurement() +
				((tags != null && tags.size() > 0)? " WHERE " + pTags : ""), DATABASE);
		
		System.err.println("SELECT * FROM " + Renewability.measurement() +
				((tags != null && tags.size() > 0)? " WHERE " + pTags : ""));
		
        QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
        results = (new InfluxDBResultMapper()).toPOJO(queryResult, Renewability.class);

 
        return results;
        //SELECT sum("renewability") FROM "myMeasurement" WHERE time >= now() - 30d and time <= now() GROUP BY time(1h) fill(null)
    }

    /**
     * SELECT A DATA BETWEEN TWO DATES
     * @param from Date of first renewability data to get | Format: yyyy-MM-dd hh:mm:ss
     * @param to Date of last renewability data to get | Format: yyyy-MM-dd hh:mm:ss
     * @return The renewability data between the from and to parameters
     */
    public List<Renewability> select(String from, String to, Map<String, String> tags) {
		List<Renewability> results = new ArrayList<Renewability>();

		// IF from VARIABLE IS A DATE TRADUCE IT TO INFLUX FORMAT ELSE IF IS A TIMESTAMP CONVERT THE TIME TO MILLISECONDS
		if (from.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
			from = '\''+from.replace(" ", "T")+'Z'+'\'';
		else from = InfluxTimeUnit.convert(from, InfluxTimeUnit.ms);
		
		if (to.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d)*"))
			to = '\''+to.replace(" ", "T")+'Z'+'\'';
		else to = InfluxTimeUnit.convert(to, InfluxTimeUnit.ms);

		try {
			String pTags = String.join(" AND ", tags.keySet().stream().map(key -> String.format("\"%s\"='%s'", key, tags.get(key))).collect(Collectors.toList()));
			pTags = '(' + pTags + ')';
			// CREATE THE QUERY TO EXECUTE
			Query query = new Query(
					String.format("SELECT * FROM %s "
							+ "WHERE time >= %s%s%s",
							Renewability.measurement(),
							from, 
							!to.equals("0ms")? " AND time <= " + to : "", 
							(tags != null && tags.size() > 0)? " AND " + pTags : "")
					, DATABASE);
			
			System.err.printf("SELECT * FROM %s "
					+ "WHERE time >= %s%s%s\n",
					Renewability.measurement(),
					from, 
					!to.equals("0ms")? " AND time <= " + to : "", 
					(tags != null && tags.size() > 0)? " AND " + pTags : "");
			// EXECUTE QUERY
	        QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
	        
	        // GET RESULTS AND TRANSLATE THEM TO THE renewability MODEL
	        results = (new InfluxDBResultMapper()).toPOJO(queryResult, Renewability.class);
	        
		} catch(InfluxDBException | InfluxDBMapperException e) {
			e.printStackTrace();
	        results = null;
		}
 
        return results;
        //SELECT sum("renewability") FROM "myMeasurement" WHERE time >= now() - 30d and time <= now() GROUP BY time(1h) fill(null)
    }

    /**
     * Group the renewability data and calculate the indicated operation
     * @param function operation to execute with Database data | See all ops in InfluxFunction enum
     * @param from Date of first renewability data to get | Format: yyyy-MM-dd hh:mm:ss
     * @param to Date of last renewability data to get | Format: yyyy-MM-dd hh:mm:ss
     * @param group Time to group the data (example: 1596039s)
     * @return The sum of renewability data group by time
     */
    public List<Renewability> operate(InfluxFunction function, String from, String to, String group, Map<String, String> tags) {
		List<Renewability> results = new ArrayList<Renewability>();
		String pTags = String.join(" AND ", tags.keySet().stream().map(key -> String.format("\"%s\"='%s'", key, tags.get(key))).collect(Collectors.toList()));
		pTags = '(' + pTags + ')';
		
		if (from.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(\\.\\d)*"))
			from = '\''+from.replace(" ", "T")+'Z'+'\'';
		else from = InfluxTimeUnit.convert(from, InfluxTimeUnit.ms);
		
		if (to.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(\\.\\d)*"))
			to = '\''+to.replace(" ", "T")+'Z'+'\'';
		else to = InfluxTimeUnit.convert(to, InfluxTimeUnit.ms);
		
		group = InfluxTimeUnit.convert(group, InfluxTimeUnit.ms);
		
		if (function != null)
			try {
				Query query = new Query(
						String.format("SELECT %s(value) as value FROM %s "
								+ "WHERE time >= %s%s%s%s",
								function.name(),
								Renewability.measurement(),
								from, 
								!to.equals("0ms")? " AND time <= " + to : "", 
								(tags != null && tags.size() > 0)? " AND " + pTags : "", 
								!group.equals("0ms")? " GROUP BY time(" + group + ") fill(linear)" : "")
						, DATABASE);
				
		        QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
		        results = (new InfluxDBResultMapper()).toPOJO(queryResult, Renewability.class);
		        
			} catch(InfluxDBException | InfluxDBMapperException e) {
				e.printStackTrace();
				results = null;
			}
		else results = null;
 
        return results;
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