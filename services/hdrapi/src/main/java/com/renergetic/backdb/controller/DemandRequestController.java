package com.renergetic.backdb.controller;

import com.renergetic.backdb.dao.DemandRequestDAO;
import com.renergetic.backdb.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.backdb.exception.InvalidNonExistingIdException;
import com.renergetic.backdb.model.*;
import com.renergetic.backdb.repository.AssetRepository;
import com.renergetic.backdb.repository.DemandRequestRepository;
import com.renergetic.backdb.repository.UserRepository;
import com.renergetic.backdb.repository.UserRolesRepository;
import com.renergetic.backdb.service.DemandRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/demandRequests")
@Tag(name = "Demand requests controller", description = "Api controller to manage demand requests: Add, update and retrieve.")
public class DemandRequestController {
    @Autowired
    DemandRequestService demandRequestService;

    @Operation(summary = "Create multiple demand requests at once")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "400", description = "One or more demand requests had an id"),
            @ApiResponse(responseCode = "500", description = "Error saving demand requests")
    })
    @PostMapping(path = "/batch", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<DemandRequestDAO>> createBatch(@RequestBody List<DemandRequestDAO> demandRequestDAOS){
        try{
            return new ResponseEntity<>(demandRequestService.save(demandRequestDAOS), HttpStatus.OK);
        } catch (InvalidCreationIdAlreadyDefinedException invalidCreationIdAlreadyDefinedException) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create one demand requests")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "400", description = "The demand requests had an id"),
            @ApiResponse(responseCode = "500", description = "Error saving demand request")
    })
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<DemandRequestDAO> create(@RequestBody DemandRequestDAO demandRequestDAO){
        try{
            return new ResponseEntity<>(demandRequestService.save(demandRequestDAO), HttpStatus.OK);
        } catch (InvalidCreationIdAlreadyDefinedException invalidCreationIdAlreadyDefinedException) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Update one demand requests")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "400", description = "The demand requests had an id"),
            @ApiResponse(responseCode = "500", description = "Error saving demand request")
    })
    @PutMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<DemandRequestDAO> update(@RequestBody DemandRequestDAO demandRequestDAO){
        try{
            return new ResponseEntity<>(demandRequestService.update(demandRequestDAO), HttpStatus.OK);
        } catch (InvalidNonExistingIdException invalidNonExistingIdException) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get all demand requests")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing demand request")
    })
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<DemandRequestDAO>> getAll(@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
        //TODO: Usage of a offset + limit
        try{
            return new ResponseEntity<>(demandRequestService.getAll(offset.orElse(0L), limit.orElse(20)), HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get one demand requests by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing demand request")
    })
    @GetMapping(path = "/id/{id}", produces = "application/json")
    public ResponseEntity<DemandRequestDAO> getById(@PathVariable Long id){
        try{
            return new ResponseEntity<>(demandRequestService.getById(id), HttpStatus.OK);
        } catch (InvalidNonExistingIdException invalidNonExistingIdException) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get list of demand requests by user id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing demand request")
    })
    @GetMapping(path = "/userId/{userId}", produces = "application/json")
    public ResponseEntity<List<DemandRequestDAO>> getByUserId(@PathVariable String userId,
                                                              @RequestParam(required = false) Optional<Long> offset,
                                                              @RequestParam(required = false) Optional<Integer> limit){
        try{
            return new ResponseEntity<>(demandRequestService.getByUserId(Long.parseLong(userId),
                    offset.orElse(0L), limit.orElse(20)), HttpStatus.OK);
        } catch (InvalidNonExistingIdException invalidNonExistingIdException) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get one demand requests by its asset id that is still valid (current time in between start and end time)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing demand request")
    })
    @GetMapping(path = "/assetId/{assetId}", produces = "application/json")
    public ResponseEntity<DemandRequestDAO> getByUuid(@PathVariable Long assetId){
        try{
            return new ResponseEntity<>(demandRequestService.getByAssetIdAndActual(assetId), HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}