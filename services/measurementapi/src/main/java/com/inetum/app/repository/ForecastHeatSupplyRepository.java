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
import org.springframework.stereotype.Service;

import com.inetum.app.model.InfluxFunction;
import com.inetum.app.model.InfluxTimeUnit;
import com.inetum.app.model.ForecastHeatConsumed;
import com.inetum.app.model.ForecastHeatSupply;

@Service
public class ForecastHeatSupplyRepository {
	@Autowired
    private InfluxDB influxDB;

	public final String DATABASE = "renergetic";

	/**
	 * Insert a power data to myMeasurement table
	 * @param power Power to set
	 */
    public void insert(ForecastHeatSupply power, Map<String, String> tags) {
        Builder registry = Point.measurementByPOJO(power.getClass())
        		.addFieldsFromPOJO(power)
        		.addField("time_prediction", power.getTimePred().toEpochMilli())
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        if (tags != null && !tags.isEmpty())
        	registry.tag(tags);
        
        influxDB.write(DATABASE, "autogen", registry.build());
    }
    
    /**
     * Get all power data from database
     * @return All power data from database as List
     */
    public List<ForecastHeatSupply> select(Map<String, String> tags) {
		List<ForecastHeatSupply> results = new ArrayList<ForecastHeatSupply>();
		String pTags = String.join(" AND ", tags.keySet().stream().map(key -> String.format("\"%s\"='%s'", key, tags.get(key))).collect(Collectors.toList()));
		pTags = '(' + pTags + ')';
		
		Query query = new Query("SELECT * FROM " + ForecastHeatSupply.measurement() +
				((tags != null && tags.size() > 0)? " WHERE " + pTags : ""), DATABASE);
		
		System.err.println("SELECT * FROM " + ForecastHeatSupply.measurement() +
				((tags != null && tags.size() > 0)? " WHERE " + pTags : ""));
		
        QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
        results = (new InfluxDBResultMapper()).toPOJO(queryResult, ForecastHeatSupply.class);

 
        return results;
        //SELECT sum("power") FROM "myMeasurement" WHERE time >= now() - 30d and time <= now() GROUP BY time(1h) fill(null)
    }

    /**
     * SELECT A DATA BETWEEN TWO DATES
     * @param from Date of first power data to get | Format: yyyy-MM-dd hh:mm:ss
     * @param to Date of last power data to get | Format: yyyy-MM-dd hh:mm:ss
     * @return The power data between the from and to parameters
     */
    public List<ForecastHeatSupply> select(String from, String to, Map<String, String> tags) {
		List<ForecastHeatSupply> results = new ArrayList<ForecastHeatSupply>();

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
							ForecastHeatSupply.measurement(),
							from, 
							!to.equals("0ms")? " AND time <= " + to : "", 
							(tags != null && tags.size() > 0)? " AND " + pTags : "")
					, DATABASE);
			
			System.err.printf("SELECT * FROM %s "
					+ "WHERE time >= %s%s%s\n",
					ForecastHeatSupply.measurement(),
					from, 
					!to.equals("0ms")? " AND time <= " + to : "", 
					(tags != null && tags.size() > 0)? " AND " + pTags : "");
			// EXECUTE QUERY
	        QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
	        
	        // GET RESULTS AND TRANSLATE THEM TO THE Power MODEL
	        results = (new InfluxDBResultMapper()).toPOJO(queryResult, ForecastHeatSupply.class);
	        
		} catch(InfluxDBException | InfluxDBMapperException e) {
			e.printStackTrace();
	        results = null;
		}
 
        return results;
        //SELECT sum("power") FROM "myMeasurement" WHERE time >= now() - 30d and time <= now() GROUP BY time(1h) fill(null)
    }

    /**
     * Group the power data and calculate the indicated operation
     * @param function operation to execute with Database data | See all ops in InfluxFunction enum
     * @param from Date of first power data to get | Format: yyyy-MM-dd hh:mm:ss
     * @param to Date of last power data to get | Format: yyyy-MM-dd hh:mm:ss
     * @param group Time to group the data (example: 1596039s)
     * @return The sum of power data group by time
     */
    public List<ForecastHeatSupply> operate(InfluxFunction function, String from, String to, String group, Map<String, String> tags) {
		List<ForecastHeatSupply> results = new ArrayList<ForecastHeatSupply>();
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
						String.format("SELECT %s(power) as power FROM %s "
								+ "WHERE time >= %s%s%s%s",
								function.name(),
								ForecastHeatSupply.measurement(),
								from, 
								!to.equals("0ms")? " AND time <= " + to : "", 
								(tags != null && tags.size() > 0)? " AND " + pTags : "", 
								!group.equals("0ms")? " GROUP BY time(" + group + ") fill(linear)" : "")
						, DATABASE);
				
		        QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
		        results = (new InfluxDBResultMapper()).toPOJO(queryResult, ForecastHeatSupply.class);
		        
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
