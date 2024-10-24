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

import com.renergetic.ingestionapi.model.Tags;
import com.renergetic.ingestionapi.repository.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.renergetic.ingestionapi.dao.FieldRestrictionsDAO;
import com.renergetic.common.dao.MeasurementIngestionDAO;
import com.renergetic.ingestionapi.dao.RestrictionsDAO;
import com.renergetic.ingestionapi.exception.ConnectionException;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.model.PrimitiveType;
import com.renergetic.common.model.Request;
import com.renergetic.common.model.RequestError;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.common.repository.MeasurementTypeRepository;
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
	
	private final static int INFLUX_TRIES = 5;

	public Map<MeasurementIngestionDAO, Boolean> insert(List<MeasurementIngestionDAO> measurements, String bucket, RestrictionsDAO restrictions) {
		int tries = 0;
		while (tries < INFLUX_TRIES)
			if (influxDB.ping()) {
				WriteApi api = influxDB.makeWriteApi();
				List<Point> entries = new ArrayList<>();
				
				// Create a list of entries to save
				Map<MeasurementIngestionDAO, Boolean> checkedMeasurements = Restrictions.check(measurements, restrictions);
				checkedMeasurements.forEach((key, value) -> {
					if (key != null && value != null) {	
						this.addDefaultTags(key);
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
						
						if (key.getTags() != null) {
							key.getTags().replaceAll((tagKey, tagValue) -> tagValue != null? tagValue: "none");
							registry.addTags(key.getTags());
						}
						
						key.getFields().forEach((fieldKey, fieldValue)-> {
							if (!fieldKey.equalsIgnoreCase("time")) {
								Entry<String, ?> field = Restrictions.parseField(fieldKey, fieldValue, restrictions);
					    		
								if (field.getValue() instanceof Double)
									registry.addField(field.getKey(), (Double) field.getValue());
								else if (field.getValue() instanceof Long)
									registry.addField(field.getKey(), (Long) field.getValue());
								else if (field.getValue() instanceof Boolean)
									registry.addField(field.getKey(), (Boolean) field.getValue());
								else if (field.getValue() instanceof String) {
									key.setErrorMessage("String values aren't allowed in field " + field.getKey());
									checkedMeasurements.put(key, false);
								}
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
			}
			else {
				try {
					tries +=1;
					Thread.sleep(200);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		
		throw new ConnectionException("Can't connect with InfluxDB");
	}
	
	public Map<MeasurementIngestionDAO, Boolean> insert(List<MeasurementIngestionDAO> measurements, String bucket, RestrictionsDAO restrictions, Request request) {
		Map<MeasurementIngestionDAO, Boolean> ret = insert(measurements, bucket, restrictions);
		
		Request requestInfo = logs.save(request);
		ret.forEach((measurement, inserted) -> {
			if (Boolean.FALSE.equals(inserted)) {
				logs.save(new RequestError("Object Not Inserted", measurement.getErrorMessage() != null? measurement.getErrorMessage() : "unknown", measurement.toString(), requestInfo));
			}
		});
		
		return ret;
	}
	
	public List<String> getMeasurementNames(){
		return repository.findByAssetIsNullAndAssetCategoryIsNull().stream().map(Measurement::getSensorName).collect(Collectors.toList());
	}
	
	public List<FieldRestrictionsDAO> getFieldRestrictions(){
		return typeRepository
			.findAll()
			.stream()
			.map(type -> new FieldRestrictionsDAO(type.getName(), PrimitiveType.DOUBLE, type.getUnit(), null))
			.collect(Collectors.toList());
	}
	
	public Map<String, String> getTagsRestrictions(){
		List<Tags> tags = tagsRepository.findByMeasurementIdIsNull();

		return tags.stream().collect(HashMap<String, String>::new, (m,v)->m.put(v.getKey(), v.getValue()), HashMap::putAll);
	}

	public void addDefaultTags(MeasurementIngestionDAO measurement) {
		if (measurement != null) {
			// Measurement type tag
			MeasurementType measurementType = typeRepository.findByName(measurement
				.getFields()
				.keySet()
				.stream()
				.filter((value) -> !value.equalsIgnoreCase("time"))
				.findFirst().orElse(null));
			
			if (measurementType != null)
				measurement.getTags().putIfAbsent("measurement_type", measurementType.getPhysicalName());
			else
				measurement.getTags().putIfAbsent("measurement_type", "default");
		}

	}
}
