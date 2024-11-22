package com.renergetic.kpiapi.controller;

import com.renergetic.common.model.Domain;
import com.renergetic.kpiapi.dao.DataWrapperDAO;
import com.renergetic.kpiapi.service.kpi.KPIFormula;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renergetic.kpiapi.dao.KPIDataDAO;
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
    public ResponseEntity<List<KPIFormula>> listKPIFormulas() {

        return ResponseEntity.ok(KPIFormula.listAll());
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

    @Operation(summary = " Calculate and return KPIs")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/data/calculate/{domain}", produces = "application/json")
    public ResponseEntity<DataWrapperDAO> calculateKPIs(
            @PathVariable(name = "domain", required = true) String domain,
            @RequestParam(name = "ts", required = false) Optional<Long> ts) {

        return ResponseEntity.ok(kpiSv.calculateKPIs(Domain.valueOf(domain.toLowerCase()), ts.orElse(null)));
    }
}
