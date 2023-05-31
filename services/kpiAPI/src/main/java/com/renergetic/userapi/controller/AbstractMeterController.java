package com.renergetic.userapi.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.renergetic.userapi.dao.AbstractMeterDAO;
import com.renergetic.userapi.model.Domain;
import com.renergetic.userapi.service.AbstractMeterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Abstract Meter Controller", description = "Allows add and see Abstract Meters data and configuration")
@RequestMapping("/api/meter")
public class AbstractMeterController {
	
	@Autowired
	AbstractMeterService amSv;

    @Operation(summary = "Get all Abstract Meters configuration")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<AbstractMeterDAO>> getAbstractMetersConfiguration(
			@RequestParam(required = false) Optional<Integer> offset,
            @RequestParam(required = false) Optional<Integer> limit) {
		
		return ResponseEntity.ok(amSv.getAll(offset.orElse(null), limit.orElse(null)));
	}

    @Operation(summary = "Get all Abstract Meters names and its description")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "list", produces = "application/json")
	public ResponseEntity<Map<String, String>> listAbstractMeters() {
		
		return ResponseEntity.ok(amSv.list());
	}

    @Operation(summary = "Get an Abstract Meter configuration")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "{domain}/{meter_name}", produces = "application/json")
	public ResponseEntity<AbstractMeterDAO> getAbstractMetersConfiguration(
			@PathVariable("domain") Domain domain,
			@PathVariable("meter_name") String name) {
		
		return ResponseEntity.ok(amSv.get(name, domain));
	}

    @Operation(summary = "Create an Abstract Meter configuration")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
	@PostMapping(path = "", produces = "application/json")
	public ResponseEntity<AbstractMeterDAO> createAbstractMetersConfiguration(
			@RequestBody AbstractMeterDAO body) {
		
		return ResponseEntity.ok(amSv.create(body));
	}

    @Operation(summary = "Modify an Abstract Meter configuration")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
	@PutMapping(path = "", produces = "application/json")
	public ResponseEntity<AbstractMeterDAO> updateAbstractMetersConfiguration(
			@RequestBody AbstractMeterDAO body) {
		
		return ResponseEntity.ok(amSv.update(body));
	}

    @Operation(summary = "Delete an Abstract Meter configuration")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
	@DeleteMapping(path = "", produces = "application/json")
	public ResponseEntity<AbstractMeterDAO> deleteAbstractMetersConfiguration(
			@RequestBody AbstractMeterDAO body) {
		
		return ResponseEntity.ok(amSv.delete(body));
	}
}