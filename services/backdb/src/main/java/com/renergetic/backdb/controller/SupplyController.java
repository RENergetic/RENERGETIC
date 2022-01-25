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

import com.renergetic.backdb.model.Supply;
import com.renergetic.backdb.model.information.SupplyInformation;
import com.renergetic.backdb.repository.SupplyRepository;
import com.renergetic.backdb.repository.information.SupplyInformationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Supplies Controller", description = "Allows add and see Supplies")
@RequestMapping("/api/supplies")
public class SupplyController {
	
	@Autowired
	SupplyRepository supplyRepository;
	@Autowired
	SupplyInformationRepository informationRepository;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get All Supplies")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<Supply>> getAllSupplies (){
		List<Supply> supplies = new ArrayList<Supply>();
		
		supplies = supplyRepository.findAll();
		
		return new ResponseEntity<List<Supply>>(supplies, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Supplies by name")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No supplies found with this name")
	})
	@GetMapping(path = "name/{name}", produces = "application/json")
	public ResponseEntity<List<Supply>> getSuppliesByName (@PathVariable String name){
		List<Supply> supplies = new ArrayList<Supply>();
		
		supplies = supplyRepository.findByName(name);
		
		supplies = supplies.isEmpty() ? null : supplies;
		
		return new ResponseEntity<List<Supply>>(supplies, supplies != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Supply by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No supplies found with this id")
	})
	@GetMapping(path = "id/{id}", produces = "application/json")
	public ResponseEntity<Supply> getSuppliesByLocation (@PathVariable Long id){
		Supply supply = null;
		
		supply = supplyRepository.findById(id).orElse(null);
		
		return new ResponseEntity<Supply>(supply, supply != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== INFO REQUESTS ===================================================================================
		
	@Operation(summary = "Get Information from a Supply")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Supply havent information or doesn't exists")
	})
	@GetMapping(path = "{id}/info", produces = "application/json")
	public ResponseEntity<List<SupplyInformation>> getInformationAsset (@PathVariable Long id){
		List<SupplyInformation> info = null;
		
		info = informationRepository.findBySupplyId(id);
		
		info = info.isEmpty() ? null : info;
		
		return new ResponseEntity<List<SupplyInformation>>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Information from a Supply and its id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Supply havent information or doesn't exists")
	})
	@GetMapping(path = "{supply_id}/info/{info_id}", produces = "application/json")
	public ResponseEntity<SupplyInformation> getInformationByID (@PathVariable Long supply_id, @PathVariable Long info_id){
		SupplyInformation info = null;
		
		info = informationRepository.findByIdAndSupplyId(info_id, supply_id);
		
		return new ResponseEntity<SupplyInformation>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Insert Information for a Supply")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Information saved correctly"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PostMapping(path = "{supply_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<SupplyInformation> insertInformation (@RequestBody SupplyInformation information, @PathVariable Long supply_id){
		try {
			information.setSupplyId(supply_id);
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
	@PutMapping(path = "{supply_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<SupplyInformation> updateInformation (@RequestBody SupplyInformation information){
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
	@DeleteMapping(path = "{supply_id}/info/{info_id}")
	public ResponseEntity<SupplyInformation> updateInformation (@PathVariable Long info_id){
		try {
			informationRepository.deleteById(info_id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
//=== POST REQUESTS ===================================================================================
			
	@Operation(summary = "Create a new Supply")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Supply saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving supply")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Supply> createSupply(@RequestBody Supply supply) {
		try {
			Supply _supply = supplyRepository
					.save(supply);
			return new ResponseEntity<>(_supply, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== PUT REQUESTS ====================================================================================
			
	@Operation(summary = "Update a existing Supply")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Supply saved correctly"),
			@ApiResponse(responseCode = "400", description = "Path isn't valid"),
			@ApiResponse(responseCode = "404", description = "Supply not exist"),
			@ApiResponse(responseCode = "500", description = "Error saving supply")
		}
	)
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Supply> updateSupply(@RequestBody Supply supply, @PathVariable Long id) {
		try {
			supply.setId(id);
			if (supplyRepository.update(supply, id) == 0)
				return ResponseEntity.notFound().build();
			else
				return new ResponseEntity<>(supply, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS =================================================================================
			
	@Operation(summary = "Delete a existing Supply", hidden = true)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Supply deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving supply")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteSupply(@PathVariable Long id) {
		try {
			supplyRepository.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
