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

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class IslandController {
	
	@Autowired
	IslandRepository islandRepository;
	
	@GetMapping("islands")
	public ResponseEntity<List<Island>> getAllIslands (){
		List<Island> islands = new ArrayList<Island>();
		
		islands = islandRepository.findAll();
		
		return new ResponseEntity<List<Island>>(islands, HttpStatus.OK);
	}
	
	
	@PostMapping("/islands")
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
