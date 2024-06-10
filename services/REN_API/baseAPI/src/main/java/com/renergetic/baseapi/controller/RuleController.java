package com.renergetic.baseapi.controller;

import com.renergetic.common.dao.RuleDAO;
import com.renergetic.common.dao.RuleRequestBodyDAO;
import com.renergetic.baseapi.service.RuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Rule Controller", description = "Allows to manage and retrieve asset rules")
@RequestMapping("/api/rules")
public class RuleController {
	@Autowired
	private RuleService ruleService;

	@Operation(summary = "Get All Rules")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "list", produces = "application/json")
	public ResponseEntity<List<RuleDAO>> getAllRules (){
		return new ResponseEntity<>(ruleService.getAllRulesAsset(), HttpStatus.OK);
	}

	@Operation(summary = "Create, Update or Delete Rules")
	@ApiResponse(responseCode = "200", description = "Asset category saved correctly")
	@ApiResponse(responseCode = "500", description = "Error saving asset category")
	@PostMapping(path = "/batch/update-create-delete", produces = "application/json", consumes = "application/json")
	@Transactional
	public ResponseEntity<List<RuleDAO>> createUpdateDeleteRules(@RequestBody RuleRequestBodyDAO request) {
		return new ResponseEntity<>(ruleService.createUpdateDeleteRules(request.getCreateUpdate(), request.getDelete()), HttpStatus.CREATED);
	}
}
