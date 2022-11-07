package com.renergetic.measurementapi.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.measurementapi.dao.MeasurementDAORequest;
import com.renergetic.measurementapi.dao.MeasurementDAOResponse;
import com.renergetic.measurementapi.model.InfluxFunction;
import com.renergetic.measurementapi.service.MeasurementService;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/measurement")
public class MeasurementController {

	@Autowired
	MeasurementService service;

	@Operation(summary = "Get measurement names")
	@GetMapping("")
	public ResponseEntity<List<String>> getMeasurement(
			@RequestParam("bucket") Optional<String> bucket){
		List<String> ret;
		
		ret = service.list(bucket.orElse("renergetic"));

		if (ret != null && ret.size() > 0)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Deprecated
	@Operation(summary = "Get measurement entries")
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
		tags.remove("bucket");
		tags.remove("from");
		tags.remove("to");
		
		ret = service.select(measurement, from.orElse(""), to.orElse(""), "time");

		if (ret != null && ret.size() > 0)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Get entries filter by measurements, tags or fields")
	@GetMapping("/data")
	public ResponseEntity<List<MeasurementDAOResponse>> getData(
			@RequestParam(name = "measurements", required = false) List<String> measurements, 
			@RequestParam(name = "fields", required = false) List<String> fields, 
			@RequestParam("from") Optional<String> from,
			@RequestParam("to") Optional<String> to,
			@RequestParam("bucket") Optional<String> bucket,
			@RequestParam Map<String, String> tags){
		
		List<MeasurementDAOResponse> ret;
		tags.remove("measurements");
		tags.remove("fields");
		tags.remove("bucket");
		tags.remove("from");
		tags.remove("to");
		
		Map<String, List<String>> parsedTags = tags.entrySet().stream()
				.collect(
						Collectors.toMap(
								entry -> entry.getKey(), 
								entry -> Arrays.stream(entry.getValue().split(",")).map(value -> value.trim()).collect(Collectors.toList())
								)
						); 
		
		ret = service.data(bucket.orElse("renergetic"), measurements, fields, parsedTags, from.orElse(""), to.orElse(""), "time");

		if (ret != null && ret.size() > 0)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Get entries and operate it")
	@GetMapping("/data/{function}")
	public ResponseEntity<List<MeasurementDAOResponse>> getProcessedData(
			@RequestParam("from") Optional<String> from, 
			@RequestParam("to") Optional<String> to,
			@RequestParam("bucket") Optional<String> bucket,
			@RequestParam("group") Optional<String> group,
			@RequestParam Map<String, String> tags, 
			@RequestParam(name = "measurements", required = false) List<String> measurements, 
			@RequestParam(name = "fields", required = false) List<String> fields, 
			@PathVariable(name = "function") String function){

		List<MeasurementDAOResponse> ret;
		tags.remove("measurements");
		tags.remove("fields");
		tags.remove("bucket");
		tags.remove("group");
		tags.remove("from");
		tags.remove("to");
		
		Map<String, List<String>> parsedTags = tags.entrySet().stream()
				.collect(
						Collectors.toMap(
								entry -> entry.getKey(), 
								entry -> Arrays.stream(entry.getValue().split(",")).map(value -> value.trim()).collect(Collectors.toList())
								)
						); 

		ret = service.dataOperation(bucket.orElse("renergetic"), InfluxFunction.obtain(function), measurements, fields, parsedTags, from.orElse(""), to.orElse(""), "time", group.orElse(""));

		if (ret != null && ret.size() > 0)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Deprecated
	@Operation(summary = "Get measurement entries and operate it")
	@GetMapping("/{measurement_name}/{function}")
	public ResponseEntity<List<MeasurementDAOResponse>> getProcessedMeasurement(
			@RequestParam("from") Optional<String> from, 
			@RequestParam("to") Optional<String> to,
			@RequestParam("bucket") Optional<String> bucket,
			@RequestParam("group") Optional<String> group,
			@RequestParam(value = "field", required = true) String field,
			/*@RequestParam("time_var") Optional<String> timeVar,*/
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
		
		if (ret != null && ret.size() > 0)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Insert an entry in a measurement")
	@PostMapping()
	public ResponseEntity<MeasurementDAOResponse> addMeasurement(@RequestBody MeasurementDAORequest measurement){
		service.insert(measurement);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Delete a measurement")
	@DeleteMapping("/{measurement_name}")
	public ResponseEntity<MeasurementDAOResponse> delMeasurement(
			@RequestParam("bucket") Optional<String> bucket,
			@PathVariable(name = "measurement_name") String measurementName,
			@RequestParam("from") Optional<String> from, 
			@RequestParam("to") Optional<String> to){

		MeasurementDAORequest measurement = new MeasurementDAORequest();
		measurement.setMeasurement(measurementName);
		measurement.setBucket(bucket.orElse("renergetic"));
		
		service.delete(measurement, from.orElse(""), to.orElse(""));
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
