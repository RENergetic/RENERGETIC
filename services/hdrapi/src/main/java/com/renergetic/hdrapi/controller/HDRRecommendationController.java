package com.renergetic.hdrapi.controller;

import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.hdrapi.dao.temp.HDRRecommendationDAO;
import com.renergetic.hdrapi.dao.temp.HDRRequestDAO;
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
    @Autowired
    private DummyDataGenerator dummyDataGenerator;
    @Value("${api.generate.dummy-data}")
    private Boolean generateDummy;
//=== GET REQUESTS====================================================================================

    @Operation(summary = "list recent recommendations")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/recommendations/current", produces = "application/json")
    public ResponseEntity<List<HDRRecommendationDAO>> getAllRecommendations(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {

        List<HDRRecommendationDAO> recommendations;
        if (!generateDummy)
            recommendations = hdrService.getRecent().orElse(new ArrayList<>());
        else
            recommendations = dummyDataGenerator.getRecommendations();

        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }


    @Operation(summary = "Get All Measurements")
    @ApiResponse(responseCode = "200", description = "Get measurements related with recommendation by id ")
    @GetMapping(path = "/recommendations/id/{id}/measurements", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> listMeasurements(
            @PathVariable Long id,
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {
        List<MeasurementDAOResponse> measurements ;

        if (!generateDummy)
            measurements = hdrService.getMeasurements(id);
        else
            measurements = dummyDataGenerator.getMeasurements(id);
        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }

    @Operation(summary = "list requests")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/requests/current", produces = "application/json")
    public ResponseEntity<List<HDRRequestDAO>> getAllRequests(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {

        List<HDRRequestDAO> recommendations;
        recommendations = hdrService.getRecentRequest().orElse(new ArrayList<>());
        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }

    @Operation(summary = "Get request by id")
    @ApiResponse(responseCode = "200", description = "HDR request by id ")
    @GetMapping(path = "/requests/id/{id}", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> getRequest(
            @PathVariable Long id,
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {
        var measurements = hdrService.getMeasurements(id);
        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }

    //=== UPDATE REQUESTS ===================================================================================
    @Operation(summary = "Insert Recommendation batch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PostMapping(path = "/recommendations", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Long> saveRecommendations(@PathVariable(name = "t") Optional<Long> timestamp,
                                                    @RequestBody List<HDRRecommendationDAO> recommendations) {
        Long t;
        t = timestamp.orElse(DateConverter.toEpoch(LocalDateTime.now()));
        hdrService.save(t, recommendations);
        return new ResponseEntity<>(t, HttpStatus.CREATED);
    }

    @Operation(summary = "Insert Recommendation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PutMapping(path = "/recommendations", produces = "application/json", consumes = "application/json")
    public ResponseEntity saveRecommendation(@PathVariable(name = "t", required = true) Long timestamp,
                                             @RequestBody List<HDRRecommendationDAO> recommendations) {
        hdrService.save(timestamp, recommendations);
        return new ResponseEntity(ResponseEntity.noContent(), HttpStatus.CREATED);
    }

    @Operation(summary = "Insert request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PostMapping(path = "/requests", produces = "application/json", consumes = "application/json")
    public ResponseEntity<HDRRequestDAO> saveRequest(@RequestBody HDRRequestDAO request) {

        HDRRequestDAO r = hdrService.save(request);
        return new ResponseEntity<>(r, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete Recommendation batch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting recommendations")
    })
    @DeleteMapping(path = "/recommendations", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<HDRRecommendationDAO>> deleteRecommendations(@PathVariable(name = "t") Long timestamp) {
        var dt = DateConverter.toLocalDateTime(timestamp);
        var res = hdrService.getRecommendations(dt);
        hdrService.deleteByTimestamp(LocalDateTime.now());
        if (hdrService.getRecommendations(dt).isEmpty()) {
            return new ResponseEntity<>(res, HttpStatus.OK);

        }
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Operation(summary = "Delete Request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting recommendations")
    })
    @DeleteMapping(path = "/requests", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<HDRRequestDAO>> deleteRequest(@PathVariable(name = "t") Long timestamp) {
        var dt = DateConverter.toLocalDateTime(timestamp);

        var res = hdrService.getRequests(dt);
        hdrService.deleteByTimestamp(LocalDateTime.now());
        if (hdrService.getRequests(dt).isEmpty()) {
            return new ResponseEntity<>(res, HttpStatus.OK);

        }
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
