package com.renergetic.backdb.controller;

import com.renergetic.backdb.dao.DemandDefinitionDAO;
import com.renergetic.backdb.dao.DemandScheduleDAO;
import com.renergetic.backdb.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.backdb.exception.InvalidNonExistingIdException;
import com.renergetic.backdb.service.DemandRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<DemandScheduleDAO>> createBatch(@RequestBody List<DemandScheduleDAO> demandScheduleDAOS){
        try{
            return new ResponseEntity<>(demandRequestService.save(demandScheduleDAOS), HttpStatus.OK);
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
    public ResponseEntity<DemandScheduleDAO> create(@RequestBody DemandScheduleDAO demandScheduleDAO){
        try{
            return new ResponseEntity<>(demandRequestService.save(demandScheduleDAO), HttpStatus.OK);
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
    public ResponseEntity<DemandScheduleDAO> update(@RequestBody DemandScheduleDAO demandScheduleDAO){
        try{
            return new ResponseEntity<>(demandRequestService.update(demandScheduleDAO), HttpStatus.OK);
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
    public ResponseEntity<List<DemandScheduleDAO>> getAll(@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
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
    public ResponseEntity<DemandScheduleDAO> getById(@PathVariable Long id){
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
    public ResponseEntity<List<DemandScheduleDAO>> getByUserId(@PathVariable String userId,
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
    public ResponseEntity<DemandScheduleDAO> getByUuid(@PathVariable Long assetId){
        try{
            return new ResponseEntity<>(demandRequestService.getByAssetIdAndActual(assetId), HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* Basic requests for schedule definition */

    @Operation(summary = "Get one demand definition by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing demand request")
    })
    @GetMapping(path = "/definition/{id}", produces = "application/json")
    public ResponseEntity<DemandDefinitionDAO> getDefinitionById(@PathVariable Long id){
        try{
            return new ResponseEntity<>(demandRequestService.getDefinitionById(id), HttpStatus.OK);
        } catch (InvalidNonExistingIdException invalidNonExistingIdException) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create one demand definition")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "400", description = "The demand requests had an id"),
            @ApiResponse(responseCode = "500", description = "Error saving demand request")
    })
    @PostMapping(path = "/definition", produces = "application/json", consumes = "application/json")
    public ResponseEntity<DemandDefinitionDAO> createDefinition(@RequestBody DemandDefinitionDAO demandDefinitionDAO){
        try{
            return new ResponseEntity<>(demandRequestService.saveDefinition(demandDefinitionDAO), HttpStatus.OK);
        } catch (InvalidCreationIdAlreadyDefinedException invalidCreationIdAlreadyDefinedException) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Update one demand definition")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "400", description = "The demand requests had an id"),
            @ApiResponse(responseCode = "500", description = "Error saving demand request")
    })
    @PutMapping(path = "/definition", produces = "application/json", consumes = "application/json")
    public ResponseEntity<DemandDefinitionDAO> updateDefinition(@RequestBody DemandDefinitionDAO demandDefinitionDAO){
        try{
            return new ResponseEntity<>(demandRequestService.updateDefinition(demandDefinitionDAO), HttpStatus.OK);
        } catch (InvalidNonExistingIdException invalidNonExistingIdException) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get all demand definition")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing demand request")
    })
    @GetMapping(path = "/definition", produces = "application/json")
    public ResponseEntity<List<DemandDefinitionDAO>> getAllDefinition(@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
        try{
            return new ResponseEntity<>(demandRequestService.listDefinitions(offset.orElse(0L), limit.orElse(20)), HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete one demand definition by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing demand request")
    })
    @DeleteMapping(path = "/definition/{id}", produces = "application/json")
    public ResponseEntity<Boolean> deleteDefinitionById(@PathVariable Long id){
        try{
            return new ResponseEntity<>(demandRequestService.deleteDefinition(id), HttpStatus.OK);
        } catch (InvalidNonExistingIdException invalidNonExistingIdException) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}