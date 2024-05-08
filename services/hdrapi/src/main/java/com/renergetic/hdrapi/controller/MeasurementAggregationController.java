package com.renergetic.hdrapi.controller;

import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.dao.OptimizerTypeDAO;
import com.renergetic.common.dao.aggregation.AggregationConfigurationDAO;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.hdrapi.dao.MeasurementAggregationMeasurementSelectionDAO;
import com.renergetic.hdrapi.service.MeasurementAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Deprecated // SEE DATA API
@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Measurement Aggregation Controller", description = "Allows to manage Measurements aggregation")
@RequestMapping("/api/measurementsAggregation")
public class MeasurementAggregationController {
    @Autowired
    private MeasurementAggregationService measurementAggregationService;

    @Operation(summary = "Get All Measurements aggregation for given asset id")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/list/{assetId}", produces = "application/json")
    public ResponseEntity<AggregationConfigurationDAO> getAllMeasurementsAggregationForAssetId(
            @PathVariable(name = "assetId") Long assetId) {
        return new ResponseEntity<>(measurementAggregationService.get(assetId), HttpStatus.OK);
    }

    @Operation(summary = "Save All Measurements aggregation for given asset id")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/save/{assetId}", produces = "application/json")
    public ResponseEntity<AggregationConfigurationDAO> saveAllMeasurementsAggregationForAssetId(
            @PathVariable(name = "assetId") Long assetId,
            @RequestBody AggregationConfigurationDAO aggregationConfigurationDAO) {
        return new ResponseEntity<>(measurementAggregationService.save(assetId, aggregationConfigurationDAO), HttpStatus.OK);
    }

    @Operation(summary = "Get All optimizer parameters with data for given asset id")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/optimizerParameters/{assetId}/{optimizerType}", produces = "application/json")
    public ResponseEntity<List<Map<String, Object>>> getOptimizerTypeList(
            @PathVariable(name = "assetId") Long assetId,
            @PathVariable(name = "optimizerType") String optimizerType
    ) {
        return new ResponseEntity<>(measurementAggregationService.getParametersForOptimizerType(assetId, optimizerType), HttpStatus.OK);
    }

    @Operation(summary = "Get All existing Optimizers types")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/optimizerTypes", produces = "application/json")
    public ResponseEntity<List<OptimizerTypeDAO>> getOptimizerTypeList() {
        return new ResponseEntity<>(measurementAggregationService.getOptimizerTypeList(), HttpStatus.OK);
    }

    @Operation(summary = "Get All Measurements for given asset aggregator")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/measurements/{assetId}", produces = "application/json")
    public ResponseEntity<List<MeasurementAggregationMeasurementSelectionDAO>> getOptimizerTypeList
            (@PathVariable(name = "assetId") Long assetId) {
        return new ResponseEntity<>(measurementAggregationService.getMeasurementsForAssetConnections(assetId), HttpStatus.OK);
    }

    @Operation(summary = "Get All Measurements compatible for given asset aggregator and selected measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/compatibleMeasurements/{assetId}/{measurementId}", produces = "application/json")
    public ResponseEntity<List<MeasurementAggregationMeasurementSelectionDAO>> getOptimizerTypeList
            (@PathVariable(name = "assetId") Long assetId, @PathVariable(name = "measurementId") Long measurementId) {
        return new ResponseEntity<>(measurementAggregationService.getMeasurementsForAssetConnectionsCompatible(assetId, measurementId), HttpStatus.OK);
    }
}
