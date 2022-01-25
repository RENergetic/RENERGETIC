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

import com.renergetic.backdb.model.Demand;
import com.renergetic.backdb.model.information.DemandInformation;
import com.renergetic.backdb.repository.DemandRepository;
import com.renergetic.backdb.repository.information.DemandInformationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Demands Controller", description = "Allows add and see Demands")
@RequestMapping("/api/demands")
public class DemandController {
	
	@Autowired
	DemandRepository demandRepository;
	@Autowired
	DemandInformationRepository informationRepository;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get All Demands")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<Demand>> getAllDemands (){
		List<Demand> demands = new ArrayList<Demand>();
		
		demands = demandRepository.findAll();
		
		return new ResponseEntity<List<Demand>>(demands, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Demands by name")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No demands found with this name")
	})
	@GetMapping(path = "name/{name}", produces = "application/json")
	public ResponseEntity<List<Demand>> getDemandsByName (@PathVariable String name){
		List<Demand> demands = new ArrayList<Demand>();
		
		demands = demandRepository.findByName(name);
		
		demands = demands.isEmpty() ? null : demands;
		
		return new ResponseEntity<List<Demand>>(demands, demands != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Demand by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No demands found with this id")
	})
	@GetMapping(path = "id/{id}", produces = "application/json")
	public ResponseEntity<Demand> getDemandsByLocation (@PathVariable Long id){
		Demand demand = null;
		
		demand = demandRepository.findById(id).orElse(null);
		
		return new ResponseEntity<Demand>(demand, demand != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== INFO REQUESTS ===================================================================================
		
	@Operation(summary = "Get Information from a Demand")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Demand havent information or doesn't exists")
	})
	@GetMapping(path = "{id}/info", produces = "application/json")
	public ResponseEntity<List<DemandInformation>> getInformationAsset (@PathVariable Long id){
		List<DemandInformation> info = null;
		
		info = informationRepository.findByDemandId(id);
		
		info = info.isEmpty() ? null : info;
		
		return new ResponseEntity<List<DemandInformation>>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Information from a Demand and its id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Demand havent information or doesn't exists")
	})
	@GetMapping(path = "{demand_id}/info/{info_id}", produces = "application/json")
	public ResponseEntity<DemandInformation> getInformationByID (@PathVariable Long demand_id, @PathVariable Long info_id){
		DemandInformation info = null;
		
		info = informationRepository.findByIdAndDemandId(info_id, demand_id);
		
		return new ResponseEntity<DemandInformation>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Insert Information for a Demand")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Information saved correctly"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PostMapping(path = "{demand_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<DemandInformation> insertInformation (@RequestBody DemandInformation information, @PathVariable Long demand_id){
		try {
			information.setDemandId(demand_id);
			information = informationRepository.save(information);
			return new ResponseEntity<>(information, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Update Information from its id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Information saved correctly"),
		@ApiResponse(responseCode = "400", description = "Path isn't valid"),
		@ApiResponse(responseCode = "404", description = "Information not exist"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PutMapping(path = "{demand_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<DemandInformation> updateInformation (@RequestBody DemandInformation information){
		try {
			information = informationRepository.save(information);
			return new ResponseEntity<>(information, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Delete Information from its id")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Information delete"),
		@ApiResponse(responseCode = "500", description = "Error deleting information")
	})
	@DeleteMapping(path = "{demand_id}/info/{info_id}")
	public ResponseEntity<DemandInformation> updateInformation (@PathVariable Long info_id){
		try {
			informationRepository.deleteById(info_id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
//=== POSTS REQUESTS ==================================================================================
			
	@Operation(summary = "Create a new Demand")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Demand saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving demand")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Demand> createDemand(@RequestBody Demand demand) {
		try {
			Demand _demand = demandRepository
					.save(demand);
			return new ResponseEntity<>(_demand, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== PUT REQUESTS ====================================================================================
			
	@Operation(summary = "Update a existing Demand")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Demand saved correctly"),
			@ApiResponse(responseCode = "400", description = "Path isn't valid"),
			@ApiResponse(responseCode = "404", description = "Demand not exist"),
			@ApiResponse(responseCode = "500", description = "Error saving demand")
		}
	)
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Demand> updateDemand(@RequestBody Demand demand, @PathVariable Long id) {
		try {
			demand.setId(id);
			if (demandRepository.update(demand, id) == 0)
				return ResponseEntity.notFound().build();
			else
				return new ResponseEntity<>(demand, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS =================================================================================
			
	@Operation(summary = "Delete a existing Demand", hidden = true)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Demand deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving demand")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteDemand(@PathVariable Long id) {
		try {
			demandRepository.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
