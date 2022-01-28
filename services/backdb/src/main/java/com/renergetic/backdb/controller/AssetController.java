package com.renergetic.backdb.controller;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.AssetRequest;
import com.renergetic.backdb.model.Information;
import com.renergetic.backdb.model.information.AssetInformation;
import com.renergetic.backdb.repository.AssetRepository;
import com.renergetic.backdb.repository.information.AssetInformationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Asset Controller", description = "Allows add and see Assets")
@RequestMapping("/api/assets")
public class AssetController {
	
	@Autowired
	AssetRepository assetRepository;
	@Autowired
	AssetInformationRepository informationRepository;
	
//=== GET REQUESTS====================================================================================
	
	@Operation(summary = "Get All Assets")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<Asset>> getAllAssets (){
		List<Asset> assets = new ArrayList<Asset>();
		
		assets = assetRepository.findAll();
		
		return new ResponseEntity<List<Asset>>(assets, HttpStatus.OK);
	}
	
	@Operation(summary = "Get assets by name")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No assets found with this name")
	})
	@GetMapping(path = "name/{name}", produces = "application/json")
	public ResponseEntity<List<Asset>> getAssetsByName (@PathVariable String name){
		List<Asset> assets = new ArrayList<Asset>();
		
		assets = assetRepository.findByName(name);
		
		assets = assets.isEmpty() ? null : assets;
		
		return new ResponseEntity<List<Asset>>(assets, assets != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Assets by location")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No assets found in this location")
	})
	@GetMapping(path = "location/{location}", produces = "application/json")
	public ResponseEntity<List<Asset>> getAssetsByLocation (@PathVariable String location){
		List<Asset> assets = new ArrayList<Asset>();
		
		assets = assetRepository.findByLocation(location);
		
		assets = assets.isEmpty() ? null : assets;
		
		return new ResponseEntity<List<Asset>>(assets, assets != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Asset by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No assets found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<Asset> getAssetsByLocation (@PathVariable Long id){
		Asset asset = null;
		
		asset = assetRepository.findById(id).orElse(null);
		
		return new ResponseEntity<Asset>(asset, asset != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== INFO REQUESTS ===================================================================================
		
	@Operation(summary = "Get Information from a Asset")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Assets havent information or doesn't exists")
	})
	@GetMapping(path = "{id}/info", produces = "application/json")
	public ResponseEntity<List<AssetInformation>> getInformationAsset (@PathVariable Long id){
		List<AssetInformation> info = null;
		
		info = informationRepository.findByAssetId(id);
		
		info = info.isEmpty() ? null : info;
		
		return new ResponseEntity<List<AssetInformation>>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Information from a Asset and its id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Assets havent information or doesn't exists")
	})
	@GetMapping(path = "{asset_id}/info/{info_id}", produces = "application/json")
	public ResponseEntity<AssetInformation> getInformationByID (@PathVariable Long asset_id, @PathVariable Long info_id){
		AssetInformation info = null;
		
		info = informationRepository.findByIdAndAssetId(info_id, asset_id);
		
		return new ResponseEntity<AssetInformation>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Insert Information for a Asset")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Information saved correctly"),
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PostMapping(path = "{asset_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<AssetInformation> insertInformation (@RequestBody AssetInformation information, @PathVariable Long asset_id){
		try {
			if (Information.ALLOWED_TYPES.stream().anyMatch(information.getType()::equalsIgnoreCase)) {
				information.setAssetId(asset_id);
				information = informationRepository.save(information);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
			return new ResponseEntity<>(information, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Update Information from its id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Information saved correctly"),
		@ApiResponse(responseCode = "400", description = "Path isn't valid"),
		@ApiResponse(responseCode = "404", description = "Information not exist"),
		@ApiResponse(responseCode = "422", description = "Type isn's valid"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PutMapping(path = "{asset_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<AssetInformation> updateInformation (@RequestBody AssetInformation information, @PathVariable Long asset_id){
		try {
			
			if (Information.ALLOWED_TYPES.stream().anyMatch(information.getType()::equalsIgnoreCase)) {
				information = informationRepository.save(information);
				return new ResponseEntity<>(information, HttpStatus.CREATED);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Delete Information from its id")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Information delete"),
		@ApiResponse(responseCode = "500", description = "Error deleting information")
	})
	@DeleteMapping(path = "{asset_id}/info/{info_id}")
	public ResponseEntity<AssetInformation> updateInformation (@PathVariable Long asset_id, @PathVariable Long info_id){
		try {
			informationRepository.deleteById(info_id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== POST REQUESTS ===================================================================================
	
	@Operation(summary = "Create a new Asset")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Asset saved correctly"),
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
			@ApiResponse(responseCode = "500", description = "Error saving asset")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Asset> createAsset(@RequestBody AssetRequest asset) {
		try {
			if (Asset.ALLOWED_TYPES.stream().anyMatch(asset.getType()::equalsIgnoreCase)) {	
				Asset _asset = assetRepository.save(asset.mapToEntity());
				return new ResponseEntity<>(_asset, HttpStatus.CREATED);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== PUT REQUESTS====================================================================================
		
	@Operation(summary = "Update a existing Asset")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Asset saved correctly"),
			@ApiResponse(responseCode = "404", description = "Asset not exist"),
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
			@ApiResponse(responseCode = "500", description = "Error saving asset")
		}
	)
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Asset> updateAsset(@RequestBody AssetRequest asset, @PathVariable Long id) {
		try {
			asset.setId(id);
			if (Asset.ALLOWED_TYPES.stream().anyMatch(asset.getType()::equalsIgnoreCase)) {	
				if (assetRepository.update(asset.mapToEntity(), id) == 0)
					return ResponseEntity.notFound().build();
				else
					return new ResponseEntity<>(asset.mapToEntity(), HttpStatus.OK);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS ================================================================================
			
	@Operation(summary = "Delete a existing Asset", hidden = true)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Asset deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving asset")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteAsset(@PathVariable Long id) {
		try {
			assetRepository.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
