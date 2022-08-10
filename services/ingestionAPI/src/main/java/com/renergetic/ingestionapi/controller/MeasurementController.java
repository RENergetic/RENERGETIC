package com.renergetic.ingestionapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.ingestionapi.dao.MeasurementDAO;
import com.renergetic.ingestionapi.dao.RestrictionsDAO;
import com.renergetic.ingestionapi.exception.TooLargeRequestException;
import com.renergetic.ingestionapi.service.MeasurementService;
import com.renergetic.ingestionapi.service.RestrictionsService;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class MeasurementController {

	@Autowired
	MeasurementService service;
	
	@Autowired
	private RestrictionsService restrictionsSv;
	
	@Operation(summary = "See a list of measurements requirements")
	@GetMapping("requirements")
	public ResponseEntity<RestrictionsDAO> getRequirements(){		
		return new ResponseEntity<>(restrictionsSv.get(), HttpStatus.OK);
	}
	
	@Operation(summary = "Insert entries in InfluxDB")
	@PostMapping("ingest/{bucket}")
	public ResponseEntity<Map<String, List<MeasurementDAO>>> addMeasurement(@PathVariable String bucket, @RequestBody List<MeasurementDAO> measurements){
		RestrictionsDAO restrictions = restrictionsSv.get();
		
		if (measurements.size() > restrictions.getRequestSize())
			throw new TooLargeRequestException("The request is too large, the max request size is %d", restrictions.getRequestSize());
		
		Map<String, List<MeasurementDAO>> ret = new TreeMap<>();
		ret.put("inserted", new ArrayList<>());
		ret.put("errors", new ArrayList<>());

		service.insert(measurements, bucket, restrictions).forEach((key, value) -> {
			if (value) ret.get("inserted").add(key); 
			else ret.get("errors").add(key); 
		});
		
		return new ResponseEntity<>(ret, HttpStatus.CREATED);
	}
}
