package com.renergetic.ruleevaluationservice.controller;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Rule Evaluation Controller", description = "Allows to manually trigger rule evaluation")
@RequestMapping("/api/trigger")
public class RuleEvaluationController {

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Trigger All Rule Evaluation")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<Long>> triggerAllRules (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		//TODO: Add a parameter to allow to return at the same time the data used for computation.
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	@Operation(summary = "Trigger Rule Evaluation by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "400", description = "Malformed URL"),
		@ApiResponse(responseCode = "404", description = "No alert found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<Long> triggerRuleById (@PathVariable Long id){
		//TODO: Add a parameter to allow to return at the same time the data used for computation.
		return new ResponseEntity<>(null, null != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Trigger Rule Evaluation by asset id")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "AlertThreshold saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving alert")
		}
	)
	@GetMapping(path = "asset/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Long> triggerAllAssetRules (@PathVariable Long id) {
		//TODO: Add a parameter to allow to return at the same time the data used for computation.
		return new ResponseEntity<>(null, HttpStatus.CREATED);
	}
}
