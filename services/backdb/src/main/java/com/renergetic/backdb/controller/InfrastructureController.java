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

import com.renergetic.backdb.model.Information;
import com.renergetic.backdb.model.Infrastructure;
import com.renergetic.backdb.model.InfrastructureRequest;
import com.renergetic.backdb.model.information.InfrastructureInformation;
import com.renergetic.backdb.repository.InfrastructureRepository;
import com.renergetic.backdb.repository.information.InfrastructureInformationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Infrastructure Controller", description = "Allows add and see Infrastructures")
@RequestMapping("/api/infrastructures")
public class InfrastructureController {
	
	@Autowired
	InfrastructureRepository infrastructureRepository;
	@Autowired
	InfrastructureInformationRepository informationRepository;

//=== GET REQUESTS ====================================================================================
		
	@Operation(summary = "Get All Infrastructures")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<Infrastructure>> getAllInfrastructures (){
		List<Infrastructure> infrastructures = new ArrayList<Infrastructure>();
		
		infrastructures = infrastructureRepository.findAll();
		
		return new ResponseEntity<List<Infrastructure>>(infrastructures, HttpStatus.OK);
	}
	
	@Operation(summary = "Get infrastructures by name")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No infrastructures found with this name")
	})
	@GetMapping(path = "name/{name}", produces = "application/json")
	public ResponseEntity<List<Infrastructure>> getInfrastructuresByName (@PathVariable String name){
		List<Infrastructure> infrastructures = new ArrayList<Infrastructure>();
		
		infrastructures = infrastructureRepository.findByName(name);
		
		infrastructures = infrastructures.isEmpty() ? null : infrastructures;
		
		return new ResponseEntity<List<Infrastructure>>(infrastructures, infrastructures != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Infrastructures by type")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No infrastructures found of this type")
	})
	@GetMapping(path = "type/{type}", produces = "application/json")
	public ResponseEntity<List<Infrastructure>> getInfrastructuresByType (@PathVariable String type){
		List<Infrastructure> infrastructures = new ArrayList<Infrastructure>();
		
		infrastructures = infrastructureRepository.findByType(type);		
		infrastructures = infrastructures.isEmpty() ? null : infrastructures;
		
		return new ResponseEntity<List<Infrastructure>>(infrastructures, infrastructures != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Infrastructure by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No infrastructures found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<Infrastructure> getInfrastructuresByLocation (@PathVariable Long id){
		Infrastructure infrastructure = null;
		
		infrastructure = infrastructureRepository.findById(id).orElse(null);
		
		return new ResponseEntity<Infrastructure>(infrastructure, infrastructure != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== INFO REQUESTS ===================================================================================
		
	@Operation(summary = "Get Information from a Infrastructure")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Infrastructure havent information or doesn't exists")
	})
	@GetMapping(path = "{id}/info", produces = "application/json")
	public ResponseEntity<List<InfrastructureInformation>> getInformationAsset (@PathVariable Long id){
		List<InfrastructureInformation> info = null;
		
		info = informationRepository.findByInfrastructureId(id);
		
		info = info.isEmpty() ? null : info;
		
		return new ResponseEntity<List<InfrastructureInformation>>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Information from a Infrastructure and its id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Infrastructure havent information or doesn't exists")
	})
	@GetMapping(path = "{infrastructure_id}/info/{info_id}", produces = "application/json")
	public ResponseEntity<InfrastructureInformation> getInformationByID (@PathVariable Long infrastructure_id, @PathVariable Long info_id){
		InfrastructureInformation info = null;
		
		info = informationRepository.findByIdAndInfrastructureId(info_id, infrastructure_id);
		
		return new ResponseEntity<InfrastructureInformation>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Insert Information for a Infrastructure")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Information saved correctly"),
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PostMapping(path = "{infrastructure_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<InfrastructureInformation> insertInformation (@RequestBody InfrastructureInformation information, @PathVariable Long infrastructure_id){
		try {
			if (Information.ALLOWED_TYPES.stream().anyMatch(information.getType()::equalsIgnoreCase)) {
				information.setInfrastructureId(infrastructure_id);
				information = informationRepository.save(information);
				return new ResponseEntity<>(information, HttpStatus.CREATED);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
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
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PutMapping(path = "{infrastructure_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<InfrastructureInformation> updateInformation (@RequestBody InfrastructureInformation information, @PathVariable Long infrastructure_id){
		try {
			if (Information.ALLOWED_TYPES.stream().anyMatch(information.getType()::equalsIgnoreCase)) {
				information = informationRepository.save(information);
				return new ResponseEntity<>(information, HttpStatus.OK);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Delete Information from its id")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Information deletec"),
		@ApiResponse(responseCode = "500", description = "Error deleting information")
	})
	@DeleteMapping(path = "{infrastructure_id}/info/{info_id}")
	public ResponseEntity<InfrastructureInformation> updateInformation (@PathVariable Long infrastructure_id, @PathVariable Long info_id){
		try {
			informationRepository.deleteById(info_id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== POST REQUESTS ===================================================================================
		
	@Operation(summary = "Create a new Infrastructure")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Infrastructure saved correctly"),
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
			@ApiResponse(responseCode = "500", description = "Error saving infrastructure")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Infrastructure> createInfrastructure(@RequestBody InfrastructureRequest infrastructure) {
		try {
			if (Infrastructure.ALLOWED_TYPES.stream().anyMatch(infrastructure.getType()::equalsIgnoreCase)) {
				Infrastructure _infrastructure = infrastructureRepository.save(infrastructure.mapToEntity());
				return new ResponseEntity<>(_infrastructure, HttpStatus.CREATED);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Update a existing Infrastructure")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Infrastructure saved correctly"),
			@ApiResponse(responseCode = "400", description = "Path isn't valid"),
			@ApiResponse(responseCode = "404", description = "Infrastructure not exist"),
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
			@ApiResponse(responseCode = "500", description = "Error saving infrastructure")
		}
	)
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Infrastructure> updateInfrastructure(@RequestBody InfrastructureRequest infrastructure, @PathVariable Long id) {
		try {
			if (Infrastructure.ALLOWED_TYPES.stream().anyMatch(infrastructure.getType()::equalsIgnoreCase)) {
				infrastructure.setId(id);
				if (infrastructureRepository.update(infrastructure.mapToEntity(), id) == 0)
					return ResponseEntity.notFound().build();
				else
					return new ResponseEntity<>(infrastructure.mapToEntity(), HttpStatus.CREATED);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Delete a existing Infrastructure", hidden = true)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Infrastructure deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving infrastructure")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteInfrastructure(@PathVariable Long id) {
		try {
			infrastructureRepository.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
