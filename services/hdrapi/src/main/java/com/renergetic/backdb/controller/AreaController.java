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

import com.renergetic.backdb.exception.NotFoundException;
import com.renergetic.backdb.model.Area;
import com.renergetic.backdb.service.AreaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Area Controller", description = "Allows add and see Area")
@RequestMapping("/api/area")
public class AreaController {
	
	@Autowired
	AreaService areaSv;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get All Area")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<Area>> getAllArea (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<Area> area = new ArrayList<>();
		
		area = areaSv.get(null, offset.orElse(0L), limit.orElse(20));
		
		return new ResponseEntity<>(area, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Area by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "400", description = "Malformed URL"),
		@ApiResponse(responseCode = "404", description = "No area found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<Area> getAreaById (@PathVariable Long id){
		Area area = null;
		
		area = areaSv.getById(id);
		
		return new ResponseEntity<>(area, area != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== POST REQUESTS ===================================================================================
			
	@Operation(summary = "Create a new Area")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Area saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving area")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Area> createArea(@RequestBody Area area) {
		try {
			area.setId(null);
			Area _area = areaSv.save(area);
			
			return new ResponseEntity<>(_area, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== PUT REQUESTS ====================================================================================

	@Operation(summary = "Update a existing Area")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Area saved correctly"),
			@ApiResponse(responseCode = "404", description = "Area not exist"),
			@ApiResponse(responseCode = "500", description = "Error saving area")
		}
	)
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Area> updateArea(@RequestBody Area area, @PathVariable Long id) {
		try {
			area.setId(id);
			return new ResponseEntity<>(areaSv.save(area), HttpStatus.OK);
		} catch (NotFoundException e) { 
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS =================================================================================
			
	@Operation(summary = "Delete a existing Area")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Area deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving area")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteArea(@PathVariable Long id) {
		try {
			areaSv.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}