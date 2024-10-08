package com.renergetic.hdrapi.controller;

import com.renergetic.common.dao.AssetCategoryDAO;
import com.renergetic.common.dao.AssetDAOResponse;
import com.renergetic.common.model.Asset;
import com.renergetic.hdrapi.service.AssetCategoryService;
import com.renergetic.hdrapi.service.AssetService;

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
import java.util.stream.Collectors;

@Deprecated // SEE BASE API
@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Asset Category Controller", description = "Allows to manage assets categories")
@RequestMapping("/api/assetCategories")
public class AssetCategoryController {
    @Autowired
    AssetCategoryService assetCategoryService;

    @Autowired
    AssetService assetService;
    
    @Operation(summary = "Create a new Asset Category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Asset category saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving asset category")
    }
    )
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AssetCategoryDAO> createAssetCategory(@RequestBody AssetCategoryDAO assetCategoryDAO) {
        AssetCategoryDAO assetCategory = assetCategoryService.save(assetCategoryDAO);
        return new ResponseEntity<>(assetCategory, HttpStatus.CREATED);
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
        AssetCategoryDAO assetCategory = assetCategoryService.update(assetCategoryDAO);
        return new ResponseEntity<>(assetCategory, HttpStatus.OK);
    }

    @Operation(summary = "Get all Asset Category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing the request")
    }
    )
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<AssetCategoryDAO>> getAllAssetCategory() {
        return new ResponseEntity<>(assetCategoryService.list(), HttpStatus.OK);
    }

    @Operation(summary = "Get one Asset Category by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "Asset category not exist"),
            @ApiResponse(responseCode = "500", description = "Error executing the request")
    }
    )
    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<AssetCategoryDAO> getAssetCategoryById(@PathVariable Long id) {
        return new ResponseEntity<>(assetCategoryService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Get list Asset Category partially matching the search term")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing the request")
    }
    )
    @GetMapping(path = "/search/{term}", produces = "application/json")
    public ResponseEntity<List<AssetCategoryDAO>> getAssetCategoriesByTerm(@PathVariable String term) {
        return new ResponseEntity<>(assetCategoryService.search(term), HttpStatus.OK);
    }

    @Operation(summary = "Delete Asset Category by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "500", description = "Error executing the request")
    }
    )
    @DeleteMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<Boolean> deleteAssetCategory(@PathVariable Long id) {
        boolean deleted = assetCategoryService.deleteById(id);
        return new ResponseEntity<>(deleted, deleted ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
    
    @Operation(summary = "Get all the assets that have a certain category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No asset found with asset category"),
            @ApiResponse(responseCode = "500", description = "Error executing the request")
    }
    )
    @GetMapping(path = "/{id}/assets", produces = "application/json")
    public ResponseEntity<List<AssetDAOResponse>> getAssetsByCategory(@PathVariable Long id,@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit) {
    	List<AssetDAOResponse> assets = assetService.getByCategory(id, offset.orElse(0L), limit.orElse(100));
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

}
