package com.renergetic.measurementapi.controller;

import com.renergetic.measurementapi.model.MeasurementType;
import com.renergetic.measurementapi.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Dashboard Controller", description = "Allows add and see Dashboards")
@RequestMapping("/api/dashboard")
public class DashboardController {
	
	@Autowired
	DashboardService dashboardSv;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get Dashboard unit")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "unit/{grafanaId}", produces = "application/json")
	public ResponseEntity<Map<String, String>> getDashboardUnitAndType (@PathVariable String grafanaId){
		MeasurementType dashboardUnit = dashboardSv.getDashboardUnitByGrafanaId(grafanaId);
		Map<String, String> result = new HashMap<>();
		result.put("unit", dashboardUnit != null ? dashboardUnit.getUnit() : "N/A");
		result.put("type", dashboardUnit != null ? dashboardUnit.getPhysicalName() : "N/A");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	

}
