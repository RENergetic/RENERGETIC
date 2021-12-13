package com.renergetic.buildingsService.controller;

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

import com.renergetic.buildingsService.model.Building;
import com.renergetic.buildingsService.repository.BuildingRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class BuildingController {

	@Autowired
	BuildingRepository buildingRepository;
	
	@GetMapping("buildings")
	public ResponseEntity<List<Building>> getAllBuildings (){
		List<Building> buildings = new ArrayList<Building>();
		
		buildings = buildingRepository.findAll();
		
		return new ResponseEntity<List<Building>>(buildings, HttpStatus.OK);
	}
	
	
	@PostMapping("/building")
	public ResponseEntity<Building> createBuilding(@RequestBody Building building) {
		try {
			Building _building = buildingRepository
					.save(new Building(building.getName(), building.getDescription(), building.getIdIsland()));
			return new ResponseEntity<>(_building, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("buildingsByIdIsland")
	public ResponseEntity<List<Building>> getBuildingsByIdIsland (@RequestBody long idIsland){
		List<Building> buildings = new ArrayList<Building>();
		
		buildings = buildingRepository.findByIdIsland(idIsland);
		
		return new ResponseEntity<List<Building>>(buildings, HttpStatus.OK);
	}
}
