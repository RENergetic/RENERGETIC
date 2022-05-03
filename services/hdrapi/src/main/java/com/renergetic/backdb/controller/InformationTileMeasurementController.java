package com.renergetic.backdb.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.renergetic.backdb.dao.InformationTileMeasurementDAORequest;
import com.renergetic.backdb.dao.InformationTileMeasurementDAOResponse;
import com.renergetic.backdb.exception.NotFoundException;
import com.renergetic.backdb.service.InformationTileMeasurementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "InformationTileMeasurement Controller", description = "Allows add and see Area")
@RequestMapping("/api/informationTileMeasurement")
public class InformationTileMeasurementController {
	
	@Autowired
	InformationTileMeasurementService informationTileMeasurementSv;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get All InformationTileMeasurement")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<InformationTileMeasurementDAOResponse>> getAllInformationTileMeasurement (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<InformationTileMeasurementDAOResponse> informationTileMeasurement = new ArrayList<>();
		
		informationTileMeasurement = informationTileMeasurementSv.get(null, offset.orElse(0L), limit.orElse(20));
		
		return new ResponseEntity<>(informationTileMeasurement, HttpStatus.OK);
	}
	
	@Operation(summary = "Get InformationTileMeasurement by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "400", description = "Malformed URL"),
		@ApiResponse(responseCode = "404", description = "No informationTileMeasurement found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<InformationTileMeasurementDAOResponse> getInformationTileMeasurementById (@PathVariable Long id){
		InformationTileMeasurementDAOResponse informationTileMeasurement = null;
		
		informationTileMeasurement = informationTileMeasurementSv.getById(id);
		
		return new ResponseEntity<>(informationTileMeasurement, informationTileMeasurement != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== POST REQUESTS ===================================================================================
			
	@Operation(summary = "Create a new InformationTileMeasurement")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "InformationTileMeasurement saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving informationTileMeasurement")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<InformationTileMeasurementDAOResponse> createInformationTileMeasurement(@RequestBody InformationTileMeasurementDAORequest informationTileMeasurement) {
		try {
			InformationTileMeasurementDAOResponse _informationTileMeasurement = informationTileMeasurementSv.save(informationTileMeasurement);
			
			return new ResponseEntity<>(_informationTileMeasurement, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== PUT REQUESTS ====================================================================================

	@Operation(summary = "Update a existing InformationTileMeasurement")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "InformationTileMeasurement saved correctly"),
			@ApiResponse(responseCode = "404", description = "InformationTileMeasurement not exist"),
			@ApiResponse(responseCode = "500", description = "Error saving informationTileMeasurement")
		}
	)
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<InformationTileMeasurementDAOResponse> updateInformationTileMeasurement(@RequestBody InformationTileMeasurementDAORequest informationTileMeasurement, @PathVariable Long id) {
		try {
			informationTileMeasurement.setId(id);
			return new ResponseEntity<>(informationTileMeasurementSv.update(informationTileMeasurement, id), HttpStatus.OK);
		} catch (NotFoundException e) { 
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS =================================================================================
			
	@Operation(summary = "Delete a existing InformationTileMeasurement")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "InformationTileMeasurement deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving informationTileMeasurement")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteInformationTileMeasurement(@PathVariable Long id) {
		try {
			informationTileMeasurementSv.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
