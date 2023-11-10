package com.renergetic.hdrapi.controller;

import com.renergetic.common.dao.HDRRecommendationDAO;
import com.renergetic.common.dao.MeasurementDAORequest;
import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.dao.ResourceDAO;
import com.renergetic.common.model.Details;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.information.MeasurementDetailsRepository;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.hdrapi.dao.MeasurementDAOImpl;
import com.renergetic.hdrapi.dao.details.MeasurementTagsDAO;
import com.renergetic.hdrapi.dao.details.TagDAO;
import com.renergetic.hdrapi.service.HDRRecommendationService;
import com.renergetic.hdrapi.service.MeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Measurement Controller", description = "HDR recommendations managemenent")
@RequestMapping("/api/hdr")
public class HDRRecommendationController {

    @Autowired
    HDRRecommendationService hdrService;

//=== GET REQUESTS====================================================================================

    @Operation(summary = "list recommendations")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/recommendations", produces = "application/json")
    public ResponseEntity<List<HDRRecommendationDAO>> getAllRecomendations(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {

        List<HDRRecommendationDAO> recommendations;
        recommendations = hdrService.getRecent().orElse(new ArrayList<>());

        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }

    @Operation(summary = "Get All Measurements")
    @ApiResponse(responseCode = "200", description = "Get measurements related with recommendation by id ")
    @GetMapping(path = "/recommendations/{id}", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> listMeasurements(
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
    @PostMapping(path = "recommendations", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Long> saveRecommendations(@PathVariable(name = "t") Optional<Long> timestamp,
                                                    @RequestBody List<HDRRecommendationDAO> recommendations) {
        LocalDateTime t;
        t = timestamp.map(DateConverter::toLocalDateTime).orElseGet(LocalDateTime::now);
        hdrService.save(t, recommendations);
        return new ResponseEntity<>(DateConverter.toEpoch(t), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete Recommendation batch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting recommendations")
    })
    @DeleteMapping(path = "recommendations", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<HDRRecommendationDAO>> deleteRecommendations(@PathVariable(name = "t") Long timestamp) {
        var dt = DateConverter.toLocalDateTime(timestamp);
        var res = hdrService.get(dt);
        hdrService.deleteByTimestamp(LocalDateTime.now());
        if (hdrService.get(dt).isEmpty()) {
            return new ResponseEntity<>(res, HttpStatus.OK);

        }
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
