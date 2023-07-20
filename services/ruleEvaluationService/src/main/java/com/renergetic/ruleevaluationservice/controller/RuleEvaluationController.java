package com.renergetic.ruleevaluationservice.controller;

import com.renergetic.ruleevaluationservice.dao.EvaluationResult;
import com.renergetic.ruleevaluationservice.service.RuleEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Rule Evaluation Controller", description = "Allows to manually trigger rule evaluation")
@RequestMapping("/api/trigger")
public class RuleEvaluationController {

	@Autowired
	private RuleEvaluationService ruleEvaluationService;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Trigger All Rule Evaluation")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<EvaluationResult>> triggerAllRules (){
		//TODO: Add a parameter to allow to return at the same time the data used for computation.
		return new ResponseEntity<>(ruleEvaluationService.retrieveAndExecuteAllRules(), HttpStatus.OK);
	}
	
	@Operation(summary = "Trigger Rule Evaluation by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "400", description = "Malformed URL"),
		@ApiResponse(responseCode = "404", description = "No alert found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<EvaluationResult> triggerRuleById (@PathVariable Long id){
		//TODO: Add a parameter to allow to return at the same time the data used for computation.
		EvaluationResult evaluationResult = ruleEvaluationService.executeRule(id);
		return new ResponseEntity<>(evaluationResult, evaluationResult != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Trigger Rule Evaluation by asset id")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "AlertThreshold saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving alert")
		}
	)
	@GetMapping(path = "asset/{id}", produces = "application/json")
	public ResponseEntity<List<EvaluationResult>> triggerAllAssetRules (@PathVariable Long id) {
		//TODO: Add a parameter to allow to return at the same time the data used for computation.
		return new ResponseEntity<>(ruleEvaluationService.retrieveAndExecuteAllRulesForAssetId(id), HttpStatus.OK);
	}
}
