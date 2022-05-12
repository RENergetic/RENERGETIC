package com.inetum.app.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.inetum.app.dao.MeasurementDAORequest;
import com.inetum.app.dao.MeasurementDAOResponse;
import com.inetum.app.model.InfluxFunction;
import com.inetum.app.service.MeasurementService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/measurement")
public class MeasurementController {

	@Autowired
	MeasurementService service;
	
	@GetMapping("/{measurement_name}")
	public ResponseEntity<List<MeasurementDAOResponse>> getMeasurement(
			@RequestParam("from") Optional<String> from, 
			@RequestParam("to") Optional<String> to,
			@RequestParam("bucket") Optional<String> bucket,
			/*@RequestParam("time_var") Optional<String> timeVar,*/
			@RequestParam Map<String, String> tags, @PathVariable(name = "measurement_name") String measurementName){
		
		MeasurementDAORequest measurement = new MeasurementDAORequest();
		measurement.setMeasurement(measurementName);
		measurement.setBucket(bucket.orElse("renergetic"));
		measurement.setTags(tags);
		
		List<MeasurementDAOResponse> ret;
		tags.remove("from");
		tags.remove("to");
		
		ret = service.select(measurement, from.orElse(""), to.orElse(""), "time");
		
		if (ret != null)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/{measurement_name}/{function}")
	public ResponseEntity<List<MeasurementDAOResponse>> getProcessedMeasurement(
			@RequestParam("from") Optional<String> from, 
			@RequestParam("to") Optional<String> to,
			@RequestParam("bucket") Optional<String> bucket,
			@RequestParam("group") Optional<String> group,
			@RequestParam(value = "field", required = true) String field,
			@RequestParam("time_var") Optional<String> timeVar,
			@RequestParam Map<String, String> tags, 
			@PathVariable(name = "measurement_name") String measurementName,
			@PathVariable(name = "function") String function){

		MeasurementDAORequest measurement = new MeasurementDAORequest();
		measurement.setMeasurement(measurementName);
		measurement.setBucket(bucket.orElse("renergetic"));
		measurement.setTags(tags);
		
		List<MeasurementDAOResponse> ret;
		tags.remove("from");
		tags.remove("to");
		tags.remove("group");
		tags.remove("field");
		
		ret = service.operate(measurement, InfluxFunction.obtain(function), field, from.orElse(""), to.orElse(""), group.orElse(""), "time");
		
		if (ret != null)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@PostMapping()
	public ResponseEntity<MeasurementDAOResponse> addMeasurement(@RequestBody MeasurementDAORequest measurement){
		service.insert(measurement);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
