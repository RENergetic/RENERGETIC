package com.renergetic.measurementapi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.renergetic.common.model.InfluxFunction;
import com.renergetic.measurementapi.service.ConvertService;
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
import com.renergetic.measurementapi.service.MeasurementService;
import com.renergetic.measurementapi.service.utils.ManageTags;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/measurement")
public class MeasurementController {

	@Autowired
	MeasurementService service;

	@Autowired
	ConvertService convertService;

	@Autowired
	ManageTags manageTags;

// GET REQUESTS
	
	@Operation(summary = "Get measurement names")
	@GetMapping("")
	public ResponseEntity<List<String>> getMeasurement(
			@RequestParam("bucket") Optional<String> bucket,
			@RequestParam(name = "hideNotFound") Optional<Boolean> hideNotFound){
		List<String> ret;
		
		ret = service.list(bucket.orElse("renergetic"));

		if (ret != null && !ret.isEmpty())
			return ResponseEntity.ok(ret);
		else if (hideNotFound.isPresent() && Boolean.TRUE.equals(hideNotFound.get()))
			return ResponseEntity.ok(List.of());
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Get tags keys and its values")
	@GetMapping("/tag")
	public ResponseEntity<Map<String, List<String>>> getTags(
			@RequestParam(name = "measurements", required = false) List<String> measurements, 
			@RequestParam(name = "fields", required = false) List<String> fields, 
			@RequestParam("bucket") Optional<String> bucket,
			@RequestParam("from") Optional<String> from,
			@RequestParam("to") Optional<String> to,
			@RequestParam Map<String, String> tags,
			@RequestParam(name = "hideNotFound") Optional<Boolean> hideNotFound){
		
		Map<String, List<String>> ret;
		manageTags.remove(tags,
		"measurements",
			"fields",
			"bucket",
			"from",
			"to",
			"hideNotFound");
		
		Map<String, List<String>> parsedTags = manageTags.parse(tags);
		
		ret = service.listTags(bucket.orElse("renergetic"), measurements, fields, parsedTags, from.orElse(null), to.orElse(null));

		if (ret != null && !ret.isEmpty())
			return ResponseEntity.ok(ret);
		else if (hideNotFound.isPresent() && Boolean.TRUE.equals(hideNotFound.get()))
			return ResponseEntity.ok(Map.of());
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Get a tag keys and its values")
	@GetMapping("/tag/{tag_name}")
	public ResponseEntity<Map<String, List<String>>> getTagValues(
			@PathVariable(name = "tag_name") String tagName,
			@RequestParam(name = "measurements", required = false) List<String> measurements,
			@RequestParam("from") Optional<String> from,
			@RequestParam("to") Optional<String> to,
			@RequestParam(name = "fields", required = false) List<String> fields,
			@RequestParam("bucket") Optional<String> bucket,
			@RequestParam Map<String, String> tags,
			@RequestParam(name = "hideNotFound") Optional<Boolean> hideNotFound){
		
		Map<String, List<String>> ret = new HashMap<>();
		manageTags.remove(tags,
			"measurements",
			"fields",
			"bucket",
			"from",
			"to",
			"hideNotFound");
		
		Map<String, List<String>> parsedTags = manageTags.parse(tags);
		
		ret.put(tagName, service.listTagValues(bucket.orElse("renergetic"), tagName, measurements, fields, parsedTags, from.orElse(null), to.orElse(null)));

		if (!ret.isEmpty())
			return ResponseEntity.ok(ret);
		else if (hideNotFound.isPresent() && Boolean.TRUE.equals(hideNotFound.get()))
			return ResponseEntity.ok(Map.of());
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
			@RequestParam Map<String, String> tags,
			@RequestParam(name = "hideNotFound") Optional<Boolean> hideNotFound,
			@RequestParam(name = "dashboardId") Optional<String> dashboardId,
			@RequestParam(name = "performDecumulation") Optional<Boolean> performDecumulation,
			@RequestParam(name = "getTags") Optional<Boolean> getTags,
			@RequestParam(name = "onlyLastestPrediction") Optional<Boolean> onlyLastestPrediction) {
		
		List<MeasurementDAOResponse> ret;
		manageTags.remove(tags,
			"measurements",
			"fields",
			"bucket",
			"from",
			"to",
			"hideNotFound",
			"dashboardId",
			"performDecumulation",
			"getTags",
			"onlyLastestPrediction");
		
		Map<String, List<String>> parsedTags = manageTags.parse(tags);
		
		ret = service.data(bucket.orElse("renergetic"), measurements, fields, parsedTags, from.orElse(""), to.orElse(""), "time", performDecumulation.orElse(false), getTags.orElse(true), onlyLastestPrediction.orElse(false));

		if(dashboardId.isPresent() && fields.size() == 1){
			ret = convertService.convert(ret, dashboardId.get(), fields, null);
		}

		if (ret != null && !ret.isEmpty())
			return ResponseEntity.ok(ret);
		else if (hideNotFound.isPresent() && Boolean.TRUE.equals(hideNotFound.get()))
			return ResponseEntity.ok(List.of());
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Get entries and operate it")
	@GetMapping("/data/{function}")
	public ResponseEntity<List<MeasurementDAOResponse>> getProcessedData(
			@RequestParam("from") Optional<String> from, 
			@RequestParam("to") Optional<String> to,
			@RequestParam("bucket") Optional<String> bucket,
			@RequestParam("group") Optional<String> group,
			@RequestParam("by_measurement") Optional<Boolean> byMeasurement,
			@RequestParam("to_float") Optional<Boolean> toFloat,
			@RequestParam(name = "measurements", required = false) List<String> measurements, 
			@RequestParam(name = "fields", required = false) List<String> fields,
			@RequestParam Map<String, String> tags,
			@RequestParam(name = "hideNotFound") Optional<Boolean> hideNotFound,
			@PathVariable(name = "function") String function,
			@RequestParam(name = "dashboardId") Optional<String> dashboardId,
			@RequestParam(name = "performDecumulation") Optional<Boolean> performDecumulation,
			@RequestParam(name = "getTags") Optional<Boolean> getTags,
			@RequestParam(name = "onlyLastestPrediction") Optional<Boolean> onlyLastestPrediction) {

		List<MeasurementDAOResponse> ret;
		manageTags.remove(tags,
			"measurements",
			"fields",
			"bucket",
			"group",
			"by_measurement",
			"to_float",
			"from",
			"to",
			"hideNotFound",
			"dashboardId",
			"performDecumulation",
			"getTags",
			"onlyLastestPrediction");
		
		Map<String, List<String>> parsedTags = manageTags.parse(tags);

		ret = service.dataOperation(bucket.orElse("renergetic"), InfluxFunction.obtain(function), measurements, fields, parsedTags, from.orElse(""), to.orElse(""), "time", group.orElse(""), byMeasurement.orElse(false), toFloat.orElse(false), performDecumulation.orElse(false), getTags.orElse(true), onlyLastestPrediction.orElse(false));

		//We only apply conversion if there is one field. as functions and grouping on multiple fields will cause issues.
		if(dashboardId.isPresent() && fields.size() == 1){
			ret = convertService.convert(ret, dashboardId.get(), fields, function);
		}

		if (ret != null && !ret.isEmpty())
			return ResponseEntity.ok(ret);
		else if (hideNotFound.isPresent() && Boolean.TRUE.equals(hideNotFound.get()))
			return ResponseEntity.ok(List.of());
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
// OTHER REQUESTS

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
	
// DEPRECATED REQUESTS

	@Deprecated
	@Operation(summary = "Get measurement entries")
	@GetMapping("/{measurement_name}")
	public ResponseEntity<List<MeasurementDAOResponse>> getMeasurement(
			@RequestParam("from") Optional<String> from, 
			@RequestParam("to") Optional<String> to,
			@RequestParam("bucket") Optional<String> bucket,
			/*@RequestParam("time_var") Optional<String> timeVar,*/
			@RequestParam Map<String, String> tags, @PathVariable(name = "measurement_name") String measurementName,
			@RequestParam(name = "hideNotFound") Optional<Boolean> hideNotFound){
		
		MeasurementDAORequest measurement = new MeasurementDAORequest();
		measurement.setMeasurement(measurementName);
		measurement.setBucket(bucket.orElse("renergetic"));
		measurement.setTags(tags);
		
		List<MeasurementDAOResponse> ret;
		manageTags.remove(tags,
		"bucket",
			"from",
			"to",
			"hideNotFound");
		
		ret = service.select(measurement, from.orElse(""), to.orElse(""), "time");

		if (ret != null && !ret.isEmpty())
			return ResponseEntity.ok(ret);
		else if (hideNotFound.isPresent() && Boolean.TRUE.equals(hideNotFound.get()))
			return ResponseEntity.ok(List.of());
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
			@RequestParam(name = "hideNotFound") Optional<Boolean> hideNotFound, 
			@PathVariable(name = "measurement_name") String measurementName,
			@PathVariable(name = "function") String function){

		MeasurementDAORequest measurement = new MeasurementDAORequest();
		measurement.setMeasurement(measurementName);
		measurement.setBucket(bucket.orElse("renergetic"));
		measurement.setTags(tags);
		
		List<MeasurementDAOResponse> ret;
		manageTags.remove(tags,
		"from",
			"to",
			"group",
			"field",
			"hideNotFound");
		
		ret = service.operate(measurement, InfluxFunction.obtain(function), field, from.orElse(""), to.orElse(""), group.orElse(""), "time");
		
		if (ret != null && !ret.isEmpty())
			return ResponseEntity.ok(ret);
		else if (hideNotFound.isPresent() && Boolean.TRUE.equals(hideNotFound.get()))
			return ResponseEntity.ok(List.of());
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
