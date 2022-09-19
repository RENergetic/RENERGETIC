package com.renergetic.hdrapi.controller;

import com.renergetic.hdrapi.dao.AssetCategoryDAO;
import com.renergetic.hdrapi.dao.AssetDAORequest;
import com.renergetic.hdrapi.dao.AssetDAOResponse;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.service.AssetCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Asset Category Controller", description = "Allows to manage assets categories")
@RequestMapping("/api/assetCategories")
public class AssetCategoryController {
    @Autowired
    AssetCategoryService assetCategoryService;

    @Operation(summary = "Create a new Asset Category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Asset category saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving asset category")
    }
    )
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AssetCategoryDAO> createAssetCategory(@RequestBody AssetCategoryDAO assetCategoryDAO) {
        try {
            AssetCategoryDAO assetCategory = assetCategoryService.save(assetCategoryDAO);
            return new ResponseEntity<>(assetCategory, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Update an existing Asset Category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset category saved correctly"),
            @ApiResponse(responseCode = "404", description = "Asset category not exist"),
            @ApiResponse(responseCode = "500", description = "Error saving asset category")
    }
    )
    @PutMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AssetCategoryDAO> updateAssetCategory(@RequestBody AssetCategoryDAO assetCategoryDAO) {
        try {
            AssetCategoryDAO assetCategory = assetCategoryService.update(assetCategoryDAO);
            return new ResponseEntity<>(assetCategory, HttpStatus.OK);
        } catch (InvalidNonExistingIdException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get all Asset Category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing the request")
    }
    )
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<AssetCategoryDAO>> getAllAssetCategory() {
        try {
            return new ResponseEntity<>(assetCategoryService.list(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get one Asset Category by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "Asset category not exist"),
            @ApiResponse(responseCode = "500", description = "Error executing the request")
    }
    )
    @GetMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AssetCategoryDAO> getAssetCategoryById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(assetCategoryService.getById(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get list Asset Category partially matching the search term")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing the request")
    }
    )
    @GetMapping(path = "/search/{term}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<AssetCategoryDAO>> getAssetCategoriesByTerm(@PathVariable String term) {
        try {
            return new ResponseEntity<>(assetCategoryService.search(term), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete Asset Category by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing the request")
    }
    )
    @DeleteMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> deleteAssetCategory(@PathVariable Long id) {
        try {
            boolean deleted = assetCategoryService.deleteById(id);
            return new ResponseEntity<>(deleted, deleted ? HttpStatus.OK : HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
