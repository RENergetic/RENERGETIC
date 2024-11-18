package com.renergetic.hdrapi.controller;

import com.renergetic.common.dao.HDRMeasurementDAO;
import com.renergetic.common.dao.HDRRecommendationDAO;
import com.renergetic.common.dao.HDRRequestDAO;
import com.renergetic.common.dao.HDRMeasurementDAO;
import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.hdrapi.service.HDRRecommendationService;
import com.renergetic.hdrapi.service.utils.DummyDataGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Measurement Controller", description = "HDR recommendations managemenent")
@RequestMapping("/api/hdr")
public class HDRRecommendationController {

    @Autowired
    HDRRecommendationService hdrService;

    //region HDR Requests
    @Operation(summary = "List current requests")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/requests/current", produces = "application/json")
    public ResponseEntity<List<HDRRequestDAO>> getAllRequests() {
        List<HDRRequestDAO> request;
        request = hdrService.getRecentRequest();
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @Operation(summary = "Insert request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PostMapping(path = "/requests", produces = "application/json", consumes = "application/json")
    public ResponseEntity<HDRRequestDAO> saveRequest(@RequestBody HDRRequestDAO request) {

        if (request.getTimestamp() == null) {
            request.setTimestamp(DateConverter.toEpoch(LocalDateTime.now()));
        } else if (request.getTimestamp() > DateConverter.now()) {
            throw new IllegalArgumentException("future timestamp");
        }
        HDRRequestDAO r = hdrService.save(request);
        return new ResponseEntity<>(r, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete HDR Request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HDR Request deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting recommendations")
    })
    @DeleteMapping(path = "/requests", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<HDRRequestDAO>> deleteRequest(@RequestParam(name = "t") Long timestamp) {
        var res = hdrService.getRequests(timestamp);
        hdrService.deleteByTimestamp(timestamp);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    //endregion

    //region recommendations
    @Operation(summary = "List current valid recommendations")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/recommendations/current", produces = "application/json")
    public ResponseEntity<List<HDRRecommendationDAO>> getAllRecommendations() {
        List<HDRRecommendationDAO> recommendations;
        recommendations = hdrService.getRecent().orElse(new ArrayList<>());
        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }

    @Operation(summary = "Append Recommendations")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PutMapping(path = "/recommendations", produces = "application/json", consumes = "application/json")
    public ResponseEntity saveRecommendations(@RequestParam(name = "t", required = true) Long timestamp,
                                              @RequestBody List<HDRRecommendationDAO> recommendations) {
        hdrService.append(timestamp, recommendations);
        return new ResponseEntity(ResponseEntity.noContent(), HttpStatus.CREATED);
    }

    @Operation(summary = "Insert Recommendation batch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PostMapping(path = "/recommendations", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Long> saveRecommendations(@RequestParam(name = "t") Optional<Long> timestamp,
                                                    @RequestBody List<HDRRecommendationDAO> recommendations) {
        Long t;
        if (timestamp.isEmpty()) t = DateConverter.now();
        else if (timestamp.get() > DateConverter.now()) throw new IllegalArgumentException("future timestamp");
        else t = timestamp.get();
        if (hdrService.save(t, recommendations))
            return new ResponseEntity<>(t, HttpStatus.CREATED);
        return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Delete Recommendations affiliated with timestamp")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting recommendations")
    })
    @DeleteMapping(path = "/recommendations", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<HDRRecommendationDAO>> deleteRecommendations(@RequestParam(name = "t") Long timestamp) {
        var res = hdrService.getRecommendations(timestamp);
        hdrService.deleteByTimestamp(timestamp);
        if (hdrService.getRecommendations(timestamp).isEmpty()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //endregion

    //region measurements
    @Operation(summary = "List all measurements related with tag")
    @GetMapping(path = {"/recommendations/key/{key}/value/{value}/measurement",
            "/recommendations/key/{key}/measurement"}, produces = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> getMeasurements(@PathVariable String key,
                                                                        @PathVariable Optional<String> value) {
        return new ResponseEntity<>(hdrService.getRecommendationMeasurements(key, value.orElse(null)), HttpStatus.OK);
    }

    @Operation(summary = "List all measurements id related with tag")
    @GetMapping(path = {"/recommendations/key/{key}/value/{value}/measurement/id",
            "/recommendations/key/{key}/measurement/id"}, produces = "application/json")
    public ResponseEntity<List<Long>> getMeasurementIds(@PathVariable String key,
                                                        @PathVariable Optional<String> value) {
        List<MeasurementDAOResponse> measurements = hdrService.getRecommendationMeasurements(key, value.orElse(null));
        List<Long> ids = measurements.stream().map(MeasurementDAOResponse::getId).toList();
        return new ResponseEntity(ids, HttpStatus.OK);
    }

    @Operation(summary = "Get measurements related with  the HDR request timestamp")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = ""),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @GetMapping(path = "/measurement", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> getMeasurements(
            @RequestParam(name = "t", required = false) Long timestamp) {
        List<MeasurementDAOResponse> measurements = hdrService.getMeasurements(timestamp);
        return new ResponseEntity(measurements, HttpStatus.OK);
    }

    @Operation(summary = "Get measurements related with  the HDR request timestamp and specific tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = " "),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @GetMapping(path = "/measurement/key/{key}/value/{value}", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> getMeasurements(
            @RequestParam(name = "t", required = false) Long timestamp, @PathVariable String key,
            @PathVariable String value) {
        List<MeasurementDAOResponse> measurements = hdrService.getMeasurements(timestamp, key, value);
        return new ResponseEntity(measurements, HttpStatus.OK);
    }

    @Operation(summary = "Associate measurement list with HDR requests ")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Measurements assigned to the HDR request"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PostMapping(path = "/measurement", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<HDRMeasurementDAO>> setMeasurement(
            @RequestParam(name = "t", required = true) Long timestamp,
            @RequestBody List<Long> measurementIds) {
        List<HDRMeasurementDAO> hdrMeasurementDAOs = hdrService.setMeasurements(timestamp, measurementIds);
        return new ResponseEntity(hdrMeasurementDAOs, HttpStatus.CREATED);
    }

    @Operation(summary = "Associate measurement with HDR requests  ")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Measurement assigned to the HDR request"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PutMapping(path = "/measurement/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<HDRMeasurementDAO> setMeasurement(@RequestParam(name = "t", required = true) Long timestamp,
                                                            @PathVariable Long id) {
        return new ResponseEntity<>(hdrService.setMeasurement(timestamp, id), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete Measurement affiliation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @DeleteMapping(path = "/measurement/{id}", produces = "application/json")
    public ResponseEntity<HDRMeasurementDAO> deleteMeasurement(
            @RequestParam(name = "t", required = true) Long timestamp, @PathVariable Long id) {
        HDRMeasurementDAO hdrMeasurementDAO = hdrService.deleteMeasurement(id, timestamp);
        if (hdrMeasurementDAO != null) {
            return new ResponseEntity<>(hdrMeasurementDAO, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //endregion


}
