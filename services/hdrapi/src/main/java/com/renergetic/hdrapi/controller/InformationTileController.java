package com.renergetic.hdrapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renergetic.common.dao.InformationTileDAORequest;
import com.renergetic.common.dao.InformationTileDAOResponse;
import com.renergetic.hdrapi.service.InformationTileService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Information Tile Controller", description = "Allows to add and retrieve tiles")
@RequestMapping("/api/informationTile")
public class InformationTileController {
    @Autowired
    private InformationTileService informationTileService;

    @Operation(summary = "Get All Tiles by panel id")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/panel/{panelId}", produces = "application/json")
    public ResponseEntity<List<InformationTileDAOResponse>> getAllPanelsOwnerId (@PathVariable Long panelId,
                                                                                 @RequestParam(required = false) Optional<Long> offset,
                                                                                 @RequestParam(required = false) Optional<Integer> limit){
        return new ResponseEntity<>(informationTileService.getAllByPanelId(panelId, offset.orElse(0L), limit.orElse(20)), HttpStatus.OK);
    }


    @Operation(summary = "Get Tile by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "tile not found")
    })
    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<InformationTileDAOResponse> getById (@PathVariable Long id){
        return new ResponseEntity<>(informationTileService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Get Tile by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "tile not found")
    })
    @GetMapping(path = "/name/{name}", produces = "application/json")
    public ResponseEntity<InformationTileDAOResponse> getByName (@PathVariable String name){
        return new ResponseEntity<>(informationTileService.getByName(name), HttpStatus.OK);
    }

    @Operation(summary = "Create a new Information Tile to an existing Information Panel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Information Tile saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving Information Tile")
    })
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<InformationTileDAOResponse> createInformationTile(@RequestBody InformationTileDAORequest informationTileDAORequest) {
        return new ResponseEntity<>(informationTileService.save(informationTileDAORequest.getPanelId(), informationTileDAORequest),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing Information Tile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Information Tile saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving Information Tile")
    })
    @PutMapping(path = "", produces = "application/json", consumes = "application/json")
//    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<InformationTileDAOResponse> updateInformationPanel(@RequestBody InformationTileDAORequest informationTileDAORequest) {
        return new ResponseEntity<>(informationTileService.update(informationTileDAORequest), HttpStatus.OK);
    }

    @Operation(summary = "Delete tile by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Information tile deleted correctly"),
            @ApiResponse(responseCode = "404", description = "tile not found"),
            @ApiResponse(responseCode = "500", description = "Error deleting Information tile")
    })
    @DeleteMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<Boolean> getByName (@PathVariable Long id){
        return new ResponseEntity<>(informationTileService.delete(id), HttpStatus.OK);
    }
}
