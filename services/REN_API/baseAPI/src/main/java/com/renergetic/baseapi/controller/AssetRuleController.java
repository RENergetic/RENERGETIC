package com.renergetic.baseapi.controller;

import com.renergetic.common.dao.AssetRuleDAO;
import com.renergetic.common.dao.AssetRuleMultiListWithAssetsDAO;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.baseapi.service.AssetRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Asset Rule Controller", description = "Allows to manage and retrieve asset rules")
@RequestMapping("/api/assetRules")
public class AssetRuleController {
	@Autowired
	private AssetRuleService assetRuleService;

	@Operation(summary = "Get All Rule with all existing assets")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "list/all", produces = "application/json")
	public ResponseEntity<List<AssetRuleMultiListWithAssetsDAO>> getAllRules (){
		return new ResponseEntity<>(assetRuleService.getAllRulesAsset(), HttpStatus.OK);
	}

	@Operation(summary = "Get All Rule for a given asset id")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "asset/{id}", produces = "application/json")
	public ResponseEntity<List<AssetRuleDAO>> getAllRulesForAssetId (@PathVariable Long id){
		return new ResponseEntity<>(assetRuleService.getAllRulesAssetForAssetId(id), HttpStatus.OK);
	}
	
	@Operation(summary = "Get rule by id")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@ApiResponse(responseCode = "400", description = "Malformed URL")
	@ApiResponse(responseCode = "404", description = "No alert found with this id")
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<AssetRuleDAO> getRuleById (@PathVariable Long id){
		try{
			AssetRuleDAO assetRuleDAO = assetRuleService.getAssetRuleById(id);
			return new ResponseEntity<>(assetRuleDAO, HttpStatus.OK);
		} catch (NotFoundException nfe){
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Create a new Rule")
	@ApiResponse(responseCode = "201", description = "Asset category saved correctly")
	@ApiResponse(responseCode = "500", description = "Error saving asset category")
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<AssetRuleDAO> createNewRule(@RequestBody AssetRuleDAO assetCategoryDAO) {
		return new ResponseEntity<>(assetRuleService.createAssetRule(assetCategoryDAO), HttpStatus.CREATED);
	}

	@Operation(summary = "Save Rule by batch")
	@ApiResponse(responseCode = "200", description = "Asset category saved correctly")
	@ApiResponse(responseCode = "500", description = "Error saving asset category")
	@PostMapping(path = "/batch", produces = "application/json", consumes = "application/json")
	@Transactional
	public ResponseEntity<List<AssetRuleDAO>> createNewRuleBatch(@RequestBody List<AssetRuleDAO> assetCategoryDAO) {
		return new ResponseEntity<>(assetRuleService.createBatchAssetRule(assetCategoryDAO), HttpStatus.CREATED);
	}

	@Operation(summary = "Update an existing Rule")
	@ApiResponse(responseCode = "200", description = "Asset category saved correctly")
	@ApiResponse(responseCode = "404", description = "Asset category not exist")
	@ApiResponse(responseCode = "500", description = "Error saving asset category")
	@PutMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<AssetRuleDAO> updateRule(@RequestBody AssetRuleDAO assetCategoryDAO) {
		return new ResponseEntity<>(assetRuleService.updateAssetRule(assetCategoryDAO), HttpStatus.OK);
	}

	@Operation(summary = "Update and create Rule by batch")
	@ApiResponse(responseCode = "200", description = "Asset category saved correctly")
	@ApiResponse(responseCode = "500", description = "Error saving asset category")
	@PostMapping(path = "/batch/update-create", produces = "application/json", consumes = "application/json")
	@Transactional
	public ResponseEntity<List<AssetRuleDAO>> updateCreateRuleBatch(@RequestBody List<AssetRuleDAO> assetCategoryDAO) {
		return new ResponseEntity<>(assetRuleService.updateAndCreateBatchAssetRule(assetCategoryDAO), HttpStatus.CREATED);
	}

	@Operation(summary = "Update and create Rule by batch for asset id")
	@ApiResponse(responseCode = "200", description = "Asset category saved correctly")
	@ApiResponse(responseCode = "500", description = "Error saving asset category")
	@PostMapping(path = "/batch/update-create-delete/{assetId}", produces = "application/json", consumes = "application/json")
	@Transactional
	public ResponseEntity<List<AssetRuleDAO>> updateCreateRuleBatch(@PathVariable Long assetId, @RequestBody List<AssetRuleDAO> assetCategoryDAO) {
		return new ResponseEntity<>(assetRuleService.updateAndCreateBatchAssetRuleForAssetId(assetId, assetCategoryDAO), HttpStatus.CREATED);
	}

	@Operation(summary = "Delete Rule by id")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@ApiResponse(responseCode = "500", description = "Error executing the request")
	@DeleteMapping(path = "/{id}", produces = "application/json")
	public ResponseEntity<Boolean> deleteRule(@PathVariable Long id) {
		try{
			Boolean deleted = assetRuleService.deleteAssetRule(id);
			return new ResponseEntity<>(deleted, HttpStatus.OK);
		} catch (NotFoundException nfe){
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
}
