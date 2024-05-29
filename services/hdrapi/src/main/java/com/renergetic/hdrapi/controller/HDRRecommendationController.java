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

    @Operation(summary = "List current valid recommendations")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/recommendations/current", produces = "application/json")
    public ResponseEntity<List<HDRRecommendationDAO>> getAllRecommendations() {
        List<HDRRecommendationDAO> recommendations;
//        if (!generateDummy)
        recommendations = hdrService.getRecent().orElse(new ArrayList<>());
//        else
//            recommendations = dummyDataGenerator.getRecommendations();

        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }


//    @Operation(summary = "Get recommendation's measurements")
//    @ApiResponse(responseCode = "200", description = "Get all measurements related with the recommendation by id ")
//    @GetMapping(path = "/recommendations/id/{id}/measurements", produces = "application/json")
//    public ResponseEntity<List<MeasurementDAOResponse>> listMeasurements(@PathVariable Long id) {
//        List<MeasurementDAOResponse> measurements;
//        if (!generateDummy)
//            measurements = hdrService.getMeasurements(id);
//        else
//            measurements = dummyDataGenerator.getMeasurements(id);
//        return new ResponseEntity<>(measurements, HttpStatus.OK);
//    }

    @Operation(summary = "List current requests")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/requests/current", produces = "application/json")
    public ResponseEntity<List<HDRRequestDAO>> getAllRequests() {

        List<HDRRequestDAO> request;
        request = hdrService.getRecentRequest();
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

//    @Operation(summary = "Get request by id")
//    @ApiResponse(responseCode = "200", description = "HDR request by id ")
//    @GetMapping(path = "/requests/id/{id}", produces = "application/json")
//    public ResponseEntity<List<MeasurementDAOResponse>> getRequest(
//            @PathVariable Long id,
//            @RequestParam(required = false) Optional<Long> offset,
//            @RequestParam(required = false) Optional<Integer> limit) {
//        var measurements = hdrService.getMeasurements(id);
//        return new ResponseEntity<>(measurements, HttpStatus.OK);
//    }

    //=== UPDATE REQUESTS ===================================================================================
    @Operation(summary = "Insert Recommendation batch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PostMapping(path = "/recommendations", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Long> saveRecommendations(@RequestParam(name = "t") Optional<Long> timestamp,
                                                    @RequestBody List<HDRRecommendationDAO> recommendations) {
        Long t;
        t = timestamp.orElse(DateConverter.now());
        if (hdrService.save(t, recommendations))
            return new ResponseEntity<>(t, HttpStatus.CREATED);

        return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Insert Recommendation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PutMapping(path = "/recommendations", produces = "application/json", consumes = "application/json")
    public ResponseEntity saveRecommendations(@RequestParam(name = "t", required = true) Long timestamp,
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

        if (request.getTimestamp() == null) {
            request.setTimestamp(DateConverter.toEpoch(LocalDateTime.now()));
        }
        HDRRequestDAO r = hdrService.save(request);
        return new ResponseEntity<>(r, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete Recommendation batch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting recommendations")
    })
    @DeleteMapping(path = "/recommendations", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<HDRRecommendationDAO>> deleteRecommendations(@RequestParam(name = "t") Long timestamp) {
//        var dt = DateConverter.toLocalDateTime(timestamp);
        var res = hdrService.getRecommendations(timestamp);
        hdrService.deleteByTimestamp(timestamp);
        if (hdrService.getRecommendations(timestamp).isEmpty()) {
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
    public ResponseEntity<List<HDRRequestDAO>> deleteRequest(@RequestParam(name = "t") Long timestamp) {
        var dt = DateConverter.toLocalDateTime(timestamp);

        var res = hdrService.getRequests(timestamp);
        hdrService.deleteByTimestamp(timestamp);
        if (hdrService.getRequests(timestamp).isEmpty()) {
            return new ResponseEntity<>(res, HttpStatus.OK);

        }
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //#region hdrMeasurements
    @Operation(summary = "Get measurements related with  the timestamp")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @GetMapping(path = "/measurement", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> getMeasurements(
            @RequestParam(name = "t", required = true) Long timestamp) {
        List<MeasurementDAOResponse> measurements = hdrService.getMeasurements(timestamp);
        return new ResponseEntity(measurements, HttpStatus.CREATED);
    }
    @Operation(summary = "Get measurements related with  the timestamp")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @GetMapping(path = "/measurement/key/{key}/value/{value}", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> getMeasurements(
            @RequestParam(name = "t", required = true) Long timestamp, @PathVariable String key,@PathVariable String value) {
        List<MeasurementDAOResponse> measurements = hdrService.getMeasurements(timestamp,key,value);
        return new ResponseEntity(measurements, HttpStatus.OK);
    }

    @Operation(summary = "Insert Measurement ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PutMapping(path = "/measurement/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<HDRMeasurementDAO> setMeasurement(@RequestParam(name = "t", required = true) Long timestamp,
                                                            @PathVariable Long id) {
        return new ResponseEntity<>( hdrService.setMeasurement(timestamp, id), HttpStatus.OK);
    }

    @Operation(summary = "Insert Measurements ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @PostMapping(path = "/measurement", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<HDRMeasurementDAO>> setMeasurement(
            @RequestParam(name = "t", required = true) Long timestamp,
            @RequestBody List<Long> measurementIds) {
        List<HDRMeasurementDAO> hdrMeasurementDAOs = hdrService.setMeasurements(timestamp, measurementIds);
        return new ResponseEntity(hdrMeasurementDAOs, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete Measurement ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving recommendation")
    })
    @DeleteMapping(path = "/measurement/{id}", produces = "application/json")
    public ResponseEntity<HDRMeasurementDAO> deleteMeasurement(
            @RequestParam(name = "t", required = true) Long timestamp,
            @PathVariable Long id) {
        HDRMeasurementDAO hdrMeasurementDAO = hdrService.deleteMeasurement(id, timestamp);
        if (hdrMeasurementDAO != null) {
            return new ResponseEntity(hdrMeasurementDAO, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //#endregion

}
