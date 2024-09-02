package com.renergetic.kpiapi.controller;

import com.renergetic.kpiapi.service.kpi.KPIFormula;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.kpiapi.dao.KPIDataDAO;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.model.InfluxFunction;
import com.renergetic.kpiapi.service.KPIService;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "KPI Controller", description = "Allows see the KPIs timeseries and group it")
@RequestMapping("/api/kpi")
public class KPIController {
	
	@Autowired
	KPIService kpiSv;
	
    @Operation(summary = "Get an KPI data")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "formula", produces = "application/json")
	public ResponseEntity<List<KPIFormula>> listKPIFormulas( ) {

		return ResponseEntity.ok(	KPIFormula.listAll());
	}
    @Operation(summary = "Get an KPI data")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "{domain}/{meter_name}/data", produces = "application/json")
	public ResponseEntity<KPIDataDAO> getAbstractMetersData(
			@PathVariable("domain") Domain domain,
			@PathVariable("meter_name") String name,
			@RequestParam(name = "from", required = false) Optional<Long> from,
			@RequestParam(name = "to", required = false) Optional<Long> to) {

		return ResponseEntity.ok(kpiSv.get(name, domain, from.orElse(null), to.orElse(null)));
	}
	
    @Operation(summary = "Get an Abstract Meter data aggregated")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "{domain}/{meter_name}/data/{operation}", produces = "application/json")
	public ResponseEntity<KPIDataDAO> getAbstractMeterAggregatedData(
			@PathVariable("domain") Domain domain,
			@PathVariable("meter_name") String name,
			@PathVariable("operation") String operation,
			@RequestParam(name = "from", required = false) Optional<Long> from,
			@RequestParam(name = "to", required = false) Optional<Long> to,
			@RequestParam(name = "group", required = false) String group) {
		
		return ResponseEntity.ok(kpiSv.getAggregated(name, domain, InfluxFunction.obtain(operation), from.orElse(null), to.orElse(null), group));
	}
}
