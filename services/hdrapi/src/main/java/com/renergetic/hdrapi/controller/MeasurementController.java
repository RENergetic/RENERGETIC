package com.renergetic.hdrapi.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.renergetic.hdrapi.dao.MeasurementDAORequest;
import com.renergetic.hdrapi.dao.MeasurementDAOResponse;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.model.MeasurementType;
import com.renergetic.hdrapi.model.details.MeasurementDetails;
import com.renergetic.hdrapi.model.details.MeasurementTags;
import com.renergetic.hdrapi.repository.information.MeasurementDetailsRepository;
import com.renergetic.hdrapi.service.MeasurementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Measurement Controller", description = "Allows add and see Measurements")
@RequestMapping("/api/measurements")
public class MeasurementController {
	
	@Autowired
	MeasurementService measurementSv;
	@Autowired
	MeasurementDetailsRepository informationRepository;
	
//=== GET REQUESTS====================================================================================
	
	@Operation(summary = "Get All Measurements")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<MeasurementDAOResponse>> getAllMeasurements (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<MeasurementDAOResponse> measurements = new ArrayList<>();
		
		measurements = measurementSv.get(null, offset.orElse(0L), limit.orElse(20));
		
		return new ResponseEntity<>(measurements, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Measurement by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No measurements found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<Measurement> getMeasurementsById(@PathVariable Long id){
		Measurement measurement = null;
		
		measurement = measurementSv.getById(id);
		
		return new ResponseEntity<Measurement>(measurement, measurement != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get All Measurements Types")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "/type", produces = "application/json")
	public ResponseEntity<List<MeasurementType>> getAllMeasurementsTypes(@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<MeasurementType> type = new ArrayList<>();
		
		type = measurementSv.getTypes(null, offset.orElse(0L), limit.orElse(20));
		
		return new ResponseEntity<>(type, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Measurement Type by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No measurement type found with this id")
	})
	@GetMapping(path = "/type/{id}", produces = "application/json")
	public ResponseEntity<MeasurementType> getMeasurementsTypeById(@PathVariable Long id){
		MeasurementType type = null;
		
		type = measurementSv.getTypeById(id);
		
		return new ResponseEntity<>(type, type != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Measurement Timeseries from InfluxDB")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No measurement values found with this id")
	})
	@GetMapping(path = "/{measurement_id}/values", produces = "application/json")
	public ResponseEntity<String> getMeasurementsValues(@PathVariable Long measurement_id, @RequestParam Map<String, String> tags){
		try {
			String url = "http://backinflux-sv:8082/api/";
			url += measurementSv.getById(measurement_id).getName();
			
			if (tags != null && tags.size() > 0)
				url += "?" + String.join("&", 
						tags.keySet().stream()
						.map(key -> key+"="+tags.get(key))
						.collect(Collectors.toList()));
			
			RestTemplate apiInflux = new RestTemplate();		
			String data = apiInflux.getForObject(url, String.class);
			
			return new ResponseEntity<>(data, data != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch(Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

//=== INFO REQUESTS ===================================================================================
		
	@Operation(summary = "Get Details from a Measurement")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Measurements havent details or doesn't exists")
	})
	@GetMapping(path = "{id}/info", produces = "application/json")
	public ResponseEntity<List<MeasurementDetails>> getInformationMeasurement (@PathVariable Long id){
		List<MeasurementDetails> info = null;
		
		info = measurementSv.getDetailsByMeasurementId(id);
		
		info = info.isEmpty() ? null : info;
		
		return new ResponseEntity<List<MeasurementDetails>>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Insert Details for a Measurement")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Details saved correctly"),
		@ApiResponse(responseCode = "500", description = "Error saving details")
	})
	@PostMapping(path = "{measurement_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<MeasurementDetails> insertInformation (@RequestBody MeasurementDetails detail, @PathVariable Long measurement_id){
		try {
			Measurement measurement = new Measurement();
			measurement.setId(measurement_id);
			detail.setMeasurement(measurement);
			MeasurementDetails _detail = measurementSv.saveDetail(detail);
			return new ResponseEntity<>(_detail, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Update Information from its id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Details saved correctly"),
		@ApiResponse(responseCode = "400", description = "Path isn't valid"),
		@ApiResponse(responseCode = "404", description = "Detail not exist"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PutMapping(path = "{measurement_id}/info/{info_id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<MeasurementDetails> updateInformation (@RequestBody MeasurementDetails detail, @PathVariable Long measurement_id, @PathVariable Long info_id){
		try {
			Measurement measurement = new Measurement();
			measurement.setId(measurement_id);
			detail.setMeasurement(measurement);
			MeasurementDetails _detail = measurementSv.updateDetail(detail, info_id);
			return new ResponseEntity<>(_detail, _detail != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Delete Information from its id")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Information deleted"),
		@ApiResponse(responseCode = "500", description = "Error deleting information")
	})
	@DeleteMapping(path = "{measurement_id}/info/{info_id}")
	public ResponseEntity<MeasurementDetails> deleteInformation (@PathVariable Long measurement_id, @PathVariable Long info_id){
		try {
			measurementSv.deleteDetailById(info_id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	//=== TAGS REQUESTS ===================================================================================	

	@Operation(summary = "Get All Tags")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "tags", produces = "application/json")
	public ResponseEntity<List<MeasurementTags>> getAllTags (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<MeasurementTags> tags = new ArrayList<>();
		
		tags = measurementSv.getTags(null, offset.orElse(0L), limit.orElse(20));
		
		return new ResponseEntity<>(tags, HttpStatus.OK);
	}

	@Operation(summary = "Insert tags")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Tag saved correctly"),
		@ApiResponse(responseCode = "500", description = "Error saving tag")
	})
	@PostMapping(path = "tags", produces = "application/json", consumes = "application/json")
	public ResponseEntity<MeasurementTags> insertTag (@RequestBody MeasurementTags tag){
		try {
			MeasurementTags _tag = measurementSv.saveTag(tag);
			return new ResponseEntity<>(_tag, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Update tags")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Tag saved correctly"),
		@ApiResponse(responseCode = "400", description = "Path isn't valid"),
		@ApiResponse(responseCode = "404", description = "Tag not exist"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PutMapping(path = "tags/{tag_id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<MeasurementTags> updateTag (@RequestBody MeasurementTags tag, @PathVariable Long tag_id){
		try {
			MeasurementTags _tag = measurementSv.updateTag(tag, tag_id);
			return new ResponseEntity<>(_tag, _tag != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Delete tags")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Tag deleted"),
		@ApiResponse(responseCode = "500", description = "Error deleting tag")
	})
	@DeleteMapping(path = "tags/{tag_id}")
	public ResponseEntity<MeasurementDetails> deleteInformation (@PathVariable Long tag_id){
		try {
			measurementSv.deleteTagById(tag_id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
//=== POST REQUESTS ===================================================================================
	
	@Operation(summary = "Create a new Measurement")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Measurement saved correctly"),
			@ApiResponse(responseCode = "422", description = "Type isn't valid"),
			@ApiResponse(responseCode = "500", description = "Error saving measurement")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<MeasurementDAOResponse> createMeasurement(@RequestBody MeasurementDAORequest measurement) {
		try {
			MeasurementDAOResponse _measurement = measurementSv.save(measurement);
			return new ResponseEntity<>(_measurement, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Create a new Measurement Type")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Measurement Type saved correctly"),
			@ApiResponse(responseCode = "422", description = "Type isn't valid"),
			@ApiResponse(responseCode = "500", description = "Error saving measurement")
		}
	)
	@PostMapping(path = "/type", produces = "application/json", consumes = "application/json")
	public ResponseEntity<MeasurementType> createMeasurementType(@RequestBody MeasurementType type) {
		try {
			MeasurementType _type = measurementSv.saveType(type);
			return new ResponseEntity<>(_type, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== PUT REQUESTS====================================================================================
		
	@Operation(summary = "Update a existing Measurement")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Measurement saved correctly"),
			@ApiResponse(responseCode = "404", description = "Measurement not exist"),
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
			@ApiResponse(responseCode = "500", description = "Error saving measurement")
		}
	)
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<MeasurementDAOResponse> updateMeasurement(@RequestBody MeasurementDAORequest measurement, @PathVariable Long id) {
		try {
			measurement.setId(id);	
			MeasurementDAOResponse _measurement = measurementSv.update(measurement, id);
			return new ResponseEntity<>(_measurement, _measurement != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Update a existing Measurement Type")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Measurement Type saved correctly"),
			@ApiResponse(responseCode = "404", description = "Measurement Type not exist"),
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
			@ApiResponse(responseCode = "500", description = "Error saving measurement type")
		}
	)
	@PutMapping(path = "/type/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<MeasurementType> updateMeasurementType(@RequestBody MeasurementType type, @PathVariable Long id) {
		try {
			type.setId(id);	
			MeasurementType _type = measurementSv.updateType(type, id);
			return new ResponseEntity<>(_type, _type != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS ================================================================================
			
	@Operation(summary = "Delete a existing Measurement", hidden = false)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Measurement deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving measurement")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteMeasurement(@PathVariable Long id) {
		try {
			measurementSv.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@Operation(summary = "Delete a existing Measurement", hidden = false)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Measurement deleted correctly"),
		@ApiResponse(responseCode = "500", description = "Error saving measurement")
	}
	)
	@DeleteMapping(path = "/type/{id}")
	public ResponseEntity<?> deleteMeasurementType(@PathVariable Long id) {
		try {
			measurementSv.deleteTypeById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}