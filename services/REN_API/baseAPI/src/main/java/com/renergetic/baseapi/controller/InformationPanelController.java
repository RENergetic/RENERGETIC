package com.renergetic.baseapi.controller;

import com.renergetic.common.dao.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renergetic.baseapi.service.InformationPanelService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Information Panel Controller", description = "Allows to add and retrieve panels")
@RequestMapping("/api/informationPanel")
public class InformationPanelController {
    @Autowired
    private InformationPanelService informationPanelService;

    @Operation(summary = "Get All Panels")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<InformationPanelDAOResponse>> getAllPanels(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {
        return new ResponseEntity<>(informationPanelService.getAll(offset.orElse(0L), limit.orElse(20)), HttpStatus.OK);
    }

    @Operation(summary = "Infer a measurements for Information Panel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Panel with filled measurements"),
            @ApiResponse(responseCode = "500", description = "Error getting measurements")
    })
    @PostMapping(path = "/infermeasurements", produces = "application/json", consumes = "application/json")
    public ResponseEntity<InformationPanelDAO> inferMeasurementsForPanel(
             @RequestBody InformationPanelDAO informationPanelDAORequest) {


        return new ResponseEntity<>(informationPanelService.inferMeasurements(informationPanelDAORequest ),
                HttpStatus.OK);
    }

    @Operation(summary = "Get All Panels by owner id")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/owner/{ownerId}", produces = "application/json")
    public ResponseEntity<List<InformationPanelDAOResponse>> getAllPanelsOwnerId(@PathVariable Long ownerId) {
        return new ResponseEntity<>(informationPanelService.getAll(ownerId), HttpStatus.OK);
    }

    @Operation(summary = "Get Panel by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "panel not found")
    })
    @GetMapping(path = "/id/{id}", produces = "application/json")
    public ResponseEntity<InformationPanelDAOResponse> getById(@PathVariable Long id) {
        return new ResponseEntity<>(informationPanelService.getById(id, true), HttpStatus.OK);
    }

    @Operation(summary = "Get Panel by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "panel not found")
    })
    @GetMapping(path = "/name/{name}", produces = "application/json")
    public ResponseEntity<InformationPanelDAOResponse> getByName(@PathVariable String name) {
        return new ResponseEntity<>(informationPanelService.getByName(name), HttpStatus.OK);
    }

    @Operation(summary = "Create a new Information Panel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Information Panel saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving Information Panel")
    })
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<InformationPanelDAOResponse> createInformationPanel(
            @RequestBody InformationPanelDAORequest informationPanelDAORequest) {
        informationPanelDAORequest.setId(null);
        return new ResponseEntity<>(informationPanelService.save(informationPanelDAORequest), HttpStatus.CREATED);
    }

    @Operation(summary = "Create a new Information Panel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Information Panel saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving Information Panel")
    })
    @PostMapping(path = "/name/{name}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<InformationPanelDAOResponse> createInformationPanel(@PathVariable("name") String name,
                                                                              @RequestBody InformationPanelDAO informationPanelDAORequest) {
        informationPanelDAORequest.setName(name);
        informationPanelDAORequest.setId(null);
        informationPanelDAORequest.setFeatured(false);
        return new ResponseEntity<>(informationPanelService.save(informationPanelDAORequest), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing Information Panel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Information Panel saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving Information Panel")
    })
    @PutMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<InformationPanelDAOResponse> updateInformationPanel(
            @RequestBody InformationPanelDAO informationPanelDAORequest) {

        return new ResponseEntity<>(informationPanelService.update(informationPanelDAORequest), HttpStatus.OK);
    }

    @Operation(summary = "Set dashboard visibility in the menu list")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Information Panel saved correctly. Return current state"),
            @ApiResponse(responseCode = "500", description = "Error saving Information Panel")
    })
    @PostMapping(path = "/id/{id}/featured/{featured}", produces = "application/json")
    public ResponseEntity<Boolean> setFeatured(@PathVariable("id") Long id,
                                               @PathVariable("featured") Boolean featured) {
//todo check privileges
        var currentFeatured = informationPanelService.setFeatured(id, featured);
        return new ResponseEntity<>(currentFeatured, currentFeatured == featured ? HttpStatus.OK : HttpStatus.CONFLICT);
    }

    @Operation(summary = "Get Connected assets")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset and panel connected correctly"),
            @ApiResponse(responseCode = "404", description = "Asset and panel or panel didn't found"),
            @ApiResponse(responseCode = "500", description = "Error connecting asset")})
    @GetMapping(path = "/id/{id}/asset", produces = "application/json")
    public ResponseEntity<List<SimpleAssetDAO>> listConnectedAssets(@PathVariable("id") Long id) {
        List<SimpleAssetDAO> assigned = informationPanelService.getConnectedAssets(id);
        return new ResponseEntity<>(assigned, HttpStatus.OK);
    }

    @Operation(summary = "Connect a Panel with a existing Asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset and panel connected correctly"),
            @ApiResponse(responseCode = "404", description = "Asset and panel or panel didn't found"),
            @ApiResponse(responseCode = "500", description = "Error connecting asset")})
    @PutMapping(path = "/id/{panel_id}/asset/{asset_id}", produces = "application/json")
    public ResponseEntity<Boolean> assignAsset(@PathVariable("panel_id") Long id,
                                               @PathVariable("asset_id") Long assetId) {
        Boolean assign = informationPanelService.assign(id, assetId);
        return new ResponseEntity<>(assign, assign ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Revoke Asset-Panel assignment")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Asset and panel or panel didn't found"),
            @ApiResponse(responseCode = "500", description = "Error disconnecting asset")})
    @DeleteMapping(path = "/id/{panel_id}/asset/{asset_id}", produces = "application/json")
    public ResponseEntity<Boolean> revokeAsset(@PathVariable("panel_id") Long id,
                                               @PathVariable("asset_id") Long assetId) {
        Boolean revoke = informationPanelService.revoke(id, assetId);
        return new ResponseEntity<>(revoke, revoke ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Delete Panel by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Information Panel deleted correctly"),
            @ApiResponse(responseCode = "404", description = "panel not found"),
            @ApiResponse(responseCode = "500", description = "Error deleting Information Panel")
    })
    @DeleteMapping(path = "/id/{id}", produces = "application/json")
    public ResponseEntity<Boolean> deleteByName(@PathVariable Long id) {
        return new ResponseEntity<>(informationPanelService.deleteById(id), HttpStatus.OK);
    }


}
