package com.renergetic.ruleevaluationservice.controller;

import com.renergetic.ruleevaluationservice.dao.AlertThresholdDAOResponse;
import com.renergetic.ruleevaluationservice.dao.AssetRuleDAO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Asset Rule Controller", description = "Allows to manage and retrieve asset rules")
@RequestMapping("/api/assetRules")
public class AssetRuleController {

	@Operation(summary = "Get All Rule for a given asset id")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "asset/{id}", produces = "application/json")
	public ResponseEntity<List<AssetRuleDAO>> getAllRulesForAssetId (@PathVariable Long id){
		return new ResponseEntity<>(alert, HttpStatus.OK);
	}
	
	@Operation(summary = "Get rule by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "400", description = "Malformed URL"),
		@ApiResponse(responseCode = "404", description = "No alert found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<AssetRuleDAO> getRuleById (@PathVariable Long id){
		return new ResponseEntity<>(alert, alert != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Create a new Rule")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Asset category saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving asset category")
	}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<AssetRuleDAO> createNewRule(@RequestBody AssetRuleDAO assetCategoryDAO) {
		AssetCategoryDAO assetCategory = assetCategoryService.save(assetCategoryDAO);
		return new ResponseEntity<>(assetCategory, HttpStatus.CREATED);
	}

	@Operation(summary = "Save Rule by batch")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Asset category saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving asset category")
	}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<AssetRuleDAO> createNewRule(@RequestBody List<AssetRuleDAO> assetCategoryDAO) {
		AssetCategoryDAO assetCategory = assetCategoryService.save(assetCategoryDAO);
		return new ResponseEntity<>(assetCategory, HttpStatus.CREATED);
	}

	@Operation(summary = "Update an existing Rule")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Asset category saved correctly"),
			@ApiResponse(responseCode = "404", description = "Asset category not exist"),
			@ApiResponse(responseCode = "500", description = "Error saving asset category")
	}
	)
	@PutMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<AssetRuleDAO> updateRule(@RequestBody AssetRuleDAO assetCategoryDAO) {
		AssetCategoryDAO assetCategory = assetCategoryService.update(assetCategoryDAO);
		return new ResponseEntity<>(assetCategory, HttpStatus.OK);
	}

	@Operation(summary = "Delete Rule by id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Request executed correctly"),
			@ApiResponse(responseCode = "500", description = "Error executing the request")
	}
	)
	@DeleteMapping(path = "/{id}", produces = "application/json")
	public ResponseEntity<Boolean> deleteRule(@PathVariable Long id) {
		boolean deleted = assetCategoryService.deleteById(id);
		return new ResponseEntity<>(deleted, deleted ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
}
