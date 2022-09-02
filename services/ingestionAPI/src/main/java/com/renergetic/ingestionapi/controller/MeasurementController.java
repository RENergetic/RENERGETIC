package com.renergetic.ingestionapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.ingestionapi.dao.MeasurementDAO;
import com.renergetic.ingestionapi.dao.RequestInfo;
import com.renergetic.ingestionapi.dao.RestrictionsDAO;
import com.renergetic.ingestionapi.exception.TooLargeRequestException;
import com.renergetic.ingestionapi.model.Request;
import com.renergetic.ingestionapi.service.MeasurementService;
import com.renergetic.ingestionapi.service.RestrictionsService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.Operation;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class MeasurementController {

	@Autowired
	private MeasurementService service;
	
	@Autowired
	private RestrictionsService restrictionsSv;
	
	@Operation(summary = "See a list of measurements requirements")
	@GetMapping("requirements")
	public ResponseEntity<RestrictionsDAO> getRequirements(@RequestHeader(name = HttpHeaders.ORIGIN, required = false, defaultValue = "unknown") String origin){
		Request request = new Request("GET", origin, "/api/requirements", 0, 200);
		return new ResponseEntity<>(restrictionsSv.get(request), HttpStatus.OK);
	}
	
	@Operation(summary = "Insert entries in InfluxDB")
	@PostMapping("ingest")
	public ResponseEntity<RequestInfo<MeasurementDAO>> addMeasurement(
			@RequestHeader(name = HttpHeaders.ORIGIN, required = false, defaultValue = "unknown") String origin,
			@RequestParam(required=false) Optional<String> bucket, 
			@RequestBody List<MeasurementDAO> measurements){
		
		RestrictionsDAO restrictions = restrictionsSv.get();
		Request request = new Request("POST", origin, "/api/ingest", measurements.size(), 200);
		
		if (measurements.size() > restrictions.getRequestSize())
			throw new TooLargeRequestException("The request is too large, the max request size is %d", restrictions.getRequestSize());
		
		RequestInfo<MeasurementDAO> ret = new RequestInfo<>();
		ret.setInserted(0L);
		ret.setErrors(new ArrayList<>());

		service.insert(measurements, bucket.orElse("renergetic"), restrictions, request).forEach((key, value) -> {
			if (value) ret.setInserted(ret.getInserted() + 1); 
			else ret.getErrors().add(key);
		});
		
		return new ResponseEntity<>(ret, HttpStatus.CREATED);
	}
}
