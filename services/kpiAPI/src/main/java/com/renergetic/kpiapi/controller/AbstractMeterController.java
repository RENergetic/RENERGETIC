package com.renergetic.kpiapi.controller;

import java.util.*;

import com.renergetic.kpiapi.dao.AbstractMeterIdentifier;
import com.renergetic.kpiapi.dao.AbstractMeterTypeDAO;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.renergetic.kpiapi.dao.AbstractMeterDAO;
import com.renergetic.kpiapi.dao.AbstractMeterDataDAO;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.model.InfluxFunction;
import com.renergetic.kpiapi.service.AbstractMeterDataService;
import com.renergetic.kpiapi.service.AbstractMeterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Abstract Meter Controller", description = "Allows add and see Abstract Meters data and configuration")
@RequestMapping("/api/meter")
public class AbstractMeterController {

    @Autowired
    AbstractMeterService amSv;

    @Autowired
    AbstractMeterDataService amDataSv;

    @Operation(summary = "Get all Abstract Meters configuration")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<AbstractMeterDAO>> getAbstractMetersConfiguration(
            @RequestParam(required = false) Optional<Integer> offset,
            @RequestParam(required = false) Optional<Integer> limit) {

        return ResponseEntity.ok(amSv.getAll(offset.orElse(null), limit.orElse(null)));
    }

    @Operation(summary = "Get all Abstract Meters names and its description")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "map", produces = "application/json")
    public ResponseEntity<Map<String, String>> mapAbstractMeters() {

        return ResponseEntity.ok(amSv.map());
    }

    @Operation(summary = "Get all Abstract Meters names and its description")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "list", produces = "application/json")
    public ResponseEntity<List<AbstractMeterTypeDAO>> listAbstractMeters() {

        return ResponseEntity.ok(amSv.list());
    }


    @Operation(summary = "Get all Abstract Meters names and its description")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "list/notconfigured", produces = "application/json")
    public ResponseEntity<List<AbstractMeterIdentifier>> getNotConfigured() {

        return ResponseEntity.ok(amSv.getNotConfigured());
    }

    @Operation(summary = "Get an Abstract Meter configuration")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "{domain}/{meter_name}", produces = "application/json")
    public ResponseEntity<AbstractMeterDAO> getAbstractMetersConfiguration(
            @PathVariable("domain") Domain domain,
            @PathVariable("meter_name") String name) {

        return ResponseEntity.ok(amSv.get(name, domain));
    }

    @Operation(summary = "Create an Abstract Meter configuration")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "", produces = "application/json")
    public ResponseEntity<AbstractMeterDAO> createAbstractMetersConfiguration(
            @RequestBody AbstractMeterDAO body) {

        return ResponseEntity.ok(amSv.create(body));
    }

    @Operation(summary = "Modify an Abstract Meter configuration")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = "", produces = "application/json")
    public ResponseEntity<AbstractMeterDAO> updateAbstractMetersConfiguration(
            @RequestBody AbstractMeterDAO body) {

        return ResponseEntity.ok(amSv.update(body));
    }

    @Operation(summary = "Delete an Abstract Meter configuration")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @DeleteMapping(path = "", produces = "application/json")
    public ResponseEntity<AbstractMeterDAO> deleteAbstractMetersConfiguration(
            @RequestBody AbstractMeterDAO body) {

        return ResponseEntity.ok(amSv.delete(body));
    }

    @Operation(summary = "Delete an Abstract Meter configuration")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @DeleteMapping(path = "{domain}/{meter_name}", produces = "application/json")
    public ResponseEntity<AbstractMeterDAO> deleteAbstractMetersConfigurationByParams(
            @PathVariable("domain") Domain domain,
            @PathVariable("meter_name") String name) {

        return ResponseEntity.ok(amSv.delete(name, domain));
    }

    @Operation(summary = "Get an Abstract Meter data")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "{domain}/{meter_name}/data", produces = "application/json")
    public ResponseEntity<AbstractMeterDataDAO> getAbstractMetersData(
            @PathVariable("domain") Domain domain,
            @PathVariable("meter_name") String name,
            @RequestParam(name = "from", required = false) Optional<Long> from,
            @RequestParam(name = "to", required = false) Optional<Long> to) {

        return ResponseEntity.ok(amDataSv.get(name, domain, from.orElse(null), to.orElse(null)));
    }

    @Operation(summary = "Get an Abstract Meter data aggregated")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "{domain}/{meter_name}/data/{operation}", produces = "application/json")
    public ResponseEntity<AbstractMeterDataDAO> getAbstractMeterAggregatedData(
            @PathVariable("domain") Domain domain,
            @PathVariable("meter_name") String name,
            @PathVariable("operation") String operation,
            @RequestParam(name = "from", required = false) Optional<Long> from,
            @RequestParam(name = "to", required = false) Optional<Long> to,
            @RequestParam(name = "group", required = false) String group) {

        return ResponseEntity.ok(amDataSv.getAggregated(name, domain, InfluxFunction.obtain(operation), from.orElse(null), to.orElse(null), group));
    }

    @Deprecated
    @Operation(summary = "Create data for an Abstract meter, it doesn't check the condition for does the calculation")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "{domain}/{meter_name}/data", produces = "application/json")
    public ResponseEntity<AbstractMeterDataDAO> insertAbstractMeterData(
            @PathVariable("domain") Domain domain,
            @PathVariable("meter_name") String name,
            @RequestParam(name = "from", required = false) Optional<Long> from,
            @RequestParam(name = "to", required = false) Optional<Long> to,
            @RequestParam(name = "time", required = false) Optional<Long> time) {

        return ResponseEntity.ok(amDataSv.calculateAndInsert(name, domain, from.orElse(null), to.orElse(null), time.orElse(null)));
    }

    @Deprecated
    @Operation(summary = "Create data for all configured Abstract meters, this method is executed periodically so its use isn't recommended")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/data", produces = "application/json")
    public ResponseEntity<List<AbstractMeterDataDAO>> insertAllAbstractMeterData(
            @RequestParam(name = "from", required = false) Optional<Long> from,
            @RequestParam(name = "to", required = false) Optional<Long> to,
            @RequestParam(name = "time", required = false) Optional<Long> time) {

        return ResponseEntity.ok(amDataSv.calculateAndInsertAll(from.orElse(null), to.orElse(null), time.orElse(null)));
    }

    @Operation(summary = " Calculate online the abstract meters ")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/data", produces = "application/json")
    public ResponseEntity<HashMap<String, String>> calculateAbstractMeters(
            @RequestParam(name = "from", required = false) Optional<Long> from,
            @RequestParam(name = "to", required = false) Optional<Long> to,
            @RequestParam(name = "time", required = false) Optional<Long> time) {

        return ResponseEntity.ok(amDataSv.calculateAbstractMeters(from.orElse(null), to.orElse(null), time.orElse(null)));
    }
}
