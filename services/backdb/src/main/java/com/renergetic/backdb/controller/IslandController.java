package com.renergetic.backdb.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.backdb.model.Island;
import com.renergetic.backdb.repository.IslandRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Islands Controller", description = "Allows add and see Islands")
@RequestMapping("/api/islands")
public class IslandController {
	
	@Autowired
	IslandRepository islandRepository;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get All Islands")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<Island>> getAllIslands (){
		List<Island> islands = new ArrayList<Island>();
		
		islands = islandRepository.findAll();
		
		return new ResponseEntity<List<Island>>(islands, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Islands by name")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No islands found with this name")
	})
	@GetMapping(path = "name/{name}", produces = "application/json")
	public ResponseEntity<List<Island>> getIslandsByName (@PathVariable String name){
		List<Island> islands = new ArrayList<Island>();
		
		islands = islandRepository.findByName(name);
		
		islands = islands.isEmpty() ? null : islands;
		
		return new ResponseEntity<List<Island>>(islands, islands != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Islands by location")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No islands found in this location")
	})
	@GetMapping(path = "location/{location}", produces = "application/json")
	public ResponseEntity<List<Island>> getIslandsByLocation (@PathVariable String location){
		List<Island> islands = new ArrayList<Island>();
		
		islands = islandRepository.findByLocation(location);
		
		islands = islands.isEmpty() ? null : islands;
		
		return new ResponseEntity<List<Island>>(islands, islands != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Island by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No islands found with this id")
	})
	@GetMapping(path = "id/{id}", produces = "application/json")
	public ResponseEntity<Island> getIslandsByLocation (@PathVariable Long id){
		Island island = null;
		
		island = islandRepository.findById(id).orElse(null);
		
		return new ResponseEntity<Island>(island, island != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== POST REQUESTS ===================================================================================
			
	@Operation(summary = "Create a new Island")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Island saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving island")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Island> createIsland(@RequestBody Island island) {
		try {
			Island _island = islandRepository
					.save(new Island(island.getName(), island.getLocation()));
			return new ResponseEntity<>(_island, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Update a existing Island")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Island saved correctly"),
			@ApiResponse(responseCode = "400", description = "Path isn't valid"),
			@ApiResponse(responseCode = "404", description = "Island not exist"),
			@ApiResponse(responseCode = "500", description = "Error saving island")
		}
	)

//=== PUT REQUESTS ====================================================================================
			
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Island> updateIsland(@RequestBody Island island, @PathVariable Long id) {
		try {
			island.setId(id);
			if (islandRepository.update(island, id) == 0)
				return ResponseEntity.notFound().build();
			else
				return new ResponseEntity<>(island, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS =================================================================================
			
	@Operation(summary = "Delete a existing Island", hidden = true)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Island deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving island")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteIsland(@PathVariable Long id) {
		try {
			islandRepository.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
