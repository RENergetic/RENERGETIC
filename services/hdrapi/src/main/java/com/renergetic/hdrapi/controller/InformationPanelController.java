package com.renergetic.hdrapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renergetic.hdrapi.dao.InformationPanelDAORequest;
import com.renergetic.hdrapi.dao.InformationPanelDAOResponse;
import com.renergetic.hdrapi.service.InformationPanelService;

import javax.websocket.server.PathParam;
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
    public ResponseEntity<List<InformationPanelDAOResponse>> getAllPanels (@RequestParam(required = false) Optional<Long> offset,
                                                                           @RequestParam(required = false) Optional<Integer> limit){
        return new ResponseEntity<>(informationPanelService.getAll(offset.orElse(0L), limit.orElse(20)), HttpStatus.OK);
    }

    @Operation(summary = "Get All Panels by owner id")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/owner/{ownerId}", produces = "application/json")
    public ResponseEntity<List<InformationPanelDAOResponse>> getAllPanelsOwnerId (@PathVariable Long ownerId){
        return new ResponseEntity<>(informationPanelService.getAll(ownerId), HttpStatus.OK);
    }

    @Operation(summary = "Get Panel by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "panel not found")
    })
    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<InformationPanelDAOResponse> getById (@PathVariable Long id){
        return new ResponseEntity<>(informationPanelService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Get Panel by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "panel not found")
    })
    @GetMapping(path = "/name/{name}", produces = "application/json")
    public ResponseEntity<InformationPanelDAOResponse> getByName (@PathVariable String name){
        return new ResponseEntity<>(informationPanelService.getByName(name), HttpStatus.OK);
    }

    @Operation(summary = "Create a new Information Panel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Information Panel saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving Information Panel")
    })
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<InformationPanelDAOResponse> createInformationPanel(@RequestBody InformationPanelDAORequest informationPanelDAORequest) {
        return new ResponseEntity<>(informationPanelService.save(informationPanelDAORequest), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing Information Panel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Information Panel saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving Information Panel")
    })
    @PutMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<InformationPanelDAOResponse> updateInformationPanel(@RequestBody InformationPanelDAORequest informationPanelDAORequest) {
        return new ResponseEntity<>(informationPanelService.update(informationPanelDAORequest), HttpStatus.OK);
    }

    @Operation(summary = "Set dashboard visibility in the menu list")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Information Panel saved correctly. Return current state"),
            @ApiResponse(responseCode = "500", description = "Error saving Information Panel")
    })
    @PostMapping(path = "/id/{id}/featured", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> setFeatured(@PathParam("id") Long id,
                                               @RequestBody Boolean state) {
//todo check privileges
        var currentFeatured = informationPanelService.setFeatured(id, state);
        return new ResponseEntity<>(currentFeatured, currentFeatured == state ? HttpStatus.OK : HttpStatus.CONFLICT);
    }



    @Operation(summary = "Connect a Panel with a existing Asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset and panel connected correctly"),
            @ApiResponse(responseCode = "404", description = "Asset and panel or panel didn't found"),
            @ApiResponse(responseCode = "500", description = "Error connecting asset")
    }
    )

    @PutMapping(path = "/id/{id}/asset/{assetId}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> assignAsset(@PathParam("panel_id") Long id, @PathParam("asset_id") Long assetId) {
        Boolean assign = informationPanelService.assign(id, assetId);
        return new ResponseEntity<>(assign, assign ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(path = "/id/{id}/asset/{assetId}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> revokeAsset(@PathParam("panel_id") Long id, @PathParam("asset_id") Long assetId) {
        Boolean assign = informationPanelService.revoke(id, assetId);
        return new ResponseEntity<>(assign, assign ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Delete Panel by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Information Panel deleted correctly"),
            @ApiResponse(responseCode = "404", description = "panel not found"),
            @ApiResponse(responseCode = "500", description = "Error deleting Information Panel")
    })
    @DeleteMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<Boolean> getByName (@PathVariable Long id){
        return new ResponseEntity<>(informationPanelService.deleteById(id), HttpStatus.OK);
    }
}
