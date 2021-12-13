package com.renergetic.backdb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.backdb.model.Island;
import com.renergetic.backdb.repository.IslandRepository;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Islands Controller", description = "Allows add and see Islands")
@RequestMapping("/api")
public class IslandController {
	
	@Autowired
	IslandRepository islandRepository;
	
	@Operation(summary = "Get All Islands")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "islands", produces = "application/json")
	public ResponseEntity<List<Island>> getAllIslands (){
		List<Island> islands = new ArrayList<Island>();
		
		islands = islandRepository.findAll();
		
		return new ResponseEntity<List<Island>>(islands, HttpStatus.OK);
	}
	
	@Operation(summary = "Create a new Island")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Island save correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving island")
		}
	)
	@PostMapping(path = "/islands", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Island> createIsland(@RequestBody Island island) {
		try {
			Island _island = islandRepository
					.save(new Island(island.getName(), island.getLocation()));
			return new ResponseEntity<>(_island, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
