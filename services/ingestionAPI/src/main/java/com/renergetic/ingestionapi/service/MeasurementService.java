package com.renergetic.ingestionapi.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.renergetic.ingestionapi.dao.FieldRestrictionsDAO;
import com.renergetic.ingestionapi.dao.MeasurementDAO;
import com.renergetic.ingestionapi.dao.RestrictionsDAO;
import com.renergetic.ingestionapi.exception.ConnectionException;
import com.renergetic.ingestionapi.model.PrimitiveType;
import com.renergetic.ingestionapi.model.Request;
import com.renergetic.ingestionapi.model.RequestError;
import com.renergetic.ingestionapi.repository.MeasurementRepository;
import com.renergetic.ingestionapi.repository.MeasurementTypeRepository;
import com.renergetic.ingestionapi.repository.TagsRepository;
import com.renergetic.ingestionapi.service.utils.Restrictions;

@Service
public class MeasurementService {
	@Autowired
    private InfluxDBClient influxDB;
	
	@Autowired
	private MeasurementRepository repository;
	
	@Autowired
	private MeasurementTypeRepository typeRepository;
	
	@Autowired
	private TagsRepository tagsRepository;
	
	@Autowired
	private LogsService logs;

	public Map<MeasurementDAO, Boolean> insert(List<MeasurementDAO> measurements, String bucket, RestrictionsDAO restrictions) {
		if (influxDB.ping()) {
			WriteApi api = influxDB.makeWriteApi();
			List<Point> entries = new ArrayList<>();
			
			// Create a list of entries to save
			Map<MeasurementDAO, Boolean> checkedMeasurements = Restrictions.check(measurements, restrictions);
			checkedMeasurements.forEach((key, value) -> {
				if (value) {		
					Point registry = Point.measurement(key.getMeasurement());	
					
					if (key.getFields().containsKey("time")) {
						try {
							Instant time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(key.getFields().get("time")).toInstant();
							registry.time(time.toEpochMilli(), WritePrecision.MS);
						} catch(ParseException error) {
							key.setErrorMessage("Invalid value to time field");
							checkedMeasurements.put(key, false);
						}
					} else
						registry.time(System.currentTimeMillis(), WritePrecision.MS);
					
					registry.addTags(key.getTags());
					key.getFields().forEach((fieldKey, fieldValue)-> {
						if (!fieldKey.equalsIgnoreCase("time")) {
							Entry<String, ?> field = Restrictions.parseField(fieldKey, fieldValue, restrictions);
				    		
							if (field.getValue() instanceof String)
				    			registry.addField(field.getKey(), (String) field.getValue());
				    		else if (field.getValue() instanceof Double)
				    			registry.addField(field.getKey(), (Double) field.getValue());
				    		else if (field.getValue() instanceof Long)
				    			registry.addField(field.getKey(), (Long) field.getValue());
				    		else if (field.getValue() instanceof Boolean)
				    			registry.addField(field.getKey(), (Boolean) field.getValue());
						}
					});
					
					entries.add(registry);
				}
			});
			
			// Write all entries at Influx
	    	api.writePoints(bucket == null || bucket.isBlank()
	    			? "renergetic" : bucket, 
	    			"renergetic", 
	    			entries);
			
	    	api.close();
	    	return checkedMeasurements;
		} else {
			throw new ConnectionException("Can't connect with InfluxDB");
		}
	}
	
	public Map<MeasurementDAO, Boolean> insert(List<MeasurementDAO> measurements, String bucket, RestrictionsDAO restrictions, Request request) {
		Map<MeasurementDAO, Boolean> ret = insert(measurements, bucket, restrictions);
		
		Request requestInfo = logs.save(request);
		ret.forEach((measurement, inserted) -> {
			if (!inserted) {
				logs.save(new RequestError("Object Not Inserted", measurement.getErrorMessage() != null? measurement.getErrorMessage() : "unknown", measurement.toString(), requestInfo));
			}
		});
		
		return ret;
	}
	
	public List<String> getMeasurementNames(){
		return repository.findByAssetIsNull().stream().map(measurement -> measurement.getName()).collect(Collectors.toList());
//				Arrays.asList(new String[]{"heat_pump", "energy_meter", "pv", "battery", "hot_water", "cold_water",
//				"weather", "heat_exchange", "cooling_circuits", "chiller", "heat_meter", "dh_temperature",
//				"dhw_temperature", "tapping_water", "thermostate", "temperature", "cpu", "renewability"});
	}
	
	public List<FieldRestrictionsDAO> getFieldRestrictions(){
		return typeRepository.findAll().stream().map(type -> new FieldRestrictionsDAO(type.getName(), PrimitiveType.DOUBLE, null)).collect(Collectors.toList());
//			Arrays.asList(new FieldRestrictionsDAO[]{
//			new FieldRestrictionsDAO("power", PrimitiveType.DOUBLE, null),
//			new FieldRestrictionsDAO("energy", PrimitiveType.DOUBLE, null),
//			new FieldRestrictionsDAO("percentage", PrimitiveType.INTEGER, null),
//			new FieldRestrictionsDAO("co2_eq", PrimitiveType.DOUBLE, null),
//			new FieldRestrictionsDAO("flow", PrimitiveType.DOUBLE, null),
//			new FieldRestrictionsDAO("voltage", PrimitiveType.DOUBLE, null),
//			new FieldRestrictionsDAO("temperature", PrimitiveType.DOUBLE, null),
//			new FieldRestrictionsDAO("pressure", PrimitiveType.DOUBLE, null),
//		});
	}
	
	public Map<String, String> getTagsRestrictions(){
//		Map<String, String> tags = new HashMap<>();
//		
//		tags.put("asset_name", null);
//		tags.put("prediction_window", "\\d+[Mdhms]");
//		tags.put("predictive_model", null);
//		tags.put("direction", "(?i)((in)|(out)|(none))");
//		tags.put("domain", "(?i)((heat)|(electricity))");
		tagsRepository.findAll().forEach(System.err::println);
		
		return tagsRepository.findAll().stream().collect(HashMap<String, String>::new, (m,v)->m.put(v.getKey(), v.getValue()), HashMap::putAll);
	}
}
