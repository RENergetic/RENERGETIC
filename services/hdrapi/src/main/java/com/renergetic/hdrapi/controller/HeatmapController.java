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

import com.renergetic.backdb.dao.HeatmapDAO;
import com.renergetic.backdb.exception.NotFoundException;
import com.renergetic.backdb.service.HeatmapService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Heatmap Controller", description = "Allows add and see Heatmap")
@RequestMapping("/api/heatmap")
public class HeatmapController {
	
	@Autowired
	HeatmapService heatmapSv;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get All Heatmap")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<HeatmapDAO>> getAllHeatmap (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<HeatmapDAO> heatmap = new ArrayList<>();
		
		heatmap = heatmapSv.get(null, offset.orElse(0L), limit.orElse(20));
		
		return new ResponseEntity<>(heatmap, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Heatmap by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "400", description = "Malformed URL"),
		@ApiResponse(responseCode = "404", description = "No heatmap found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<HeatmapDAO> getHeatmapById (@PathVariable Long id){
		HeatmapDAO heatmap = null;
		
		heatmap = heatmapSv.getById(id);
		
		return new ResponseEntity<>(heatmap, heatmap != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== POST REQUESTS ===================================================================================
			
	@Operation(summary = "Create a new Heatmap")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Heatmap saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving heatmap")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<HeatmapDAO> createHeatmap(@RequestBody HeatmapDAO heatmap) {
		try {
			heatmap.setId(null);
			HeatmapDAO _heatmap = heatmapSv.save(heatmap);
			
			return new ResponseEntity<>(_heatmap, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== PUT REQUESTS ====================================================================================

	@Operation(summary = "Update a existing Heatmap")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Heatmap saved correctly"),
			@ApiResponse(responseCode = "404", description = "Heatmap not exist"),
			@ApiResponse(responseCode = "500", description = "Error saving heatmap")
		}
	)
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<HeatmapDAO> updateHeatmap(@RequestBody HeatmapDAO heatmap, @PathVariable Long id) {
		try {
			heatmap.setId(id);
			return new ResponseEntity<>(heatmapSv.save(heatmap), HttpStatus.OK);
		} catch (NotFoundException e) { 
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS =================================================================================
			
	@Operation(summary = "Delete a existing Heatmap")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Heatmap deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving heatmap")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteHeatmap(@PathVariable Long id) {
		try {
			heatmapSv.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
