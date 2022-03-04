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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.backdb.dao.AssetDAORequest;
import com.renergetic.backdb.dao.AssetDAOResponse;
import com.renergetic.backdb.dao.MeasurementDAOResponse;
import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.details.AssetDetails;
import com.renergetic.backdb.repository.information.AssetDetailsRepository;
import com.renergetic.backdb.service.AssetService;

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
	AssetService assetSv;
	@Autowired
	AssetDetailsRepository informationRepository;
	
//=== GET REQUESTS====================================================================================
	
	@Operation(summary = "Get All Assets")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<AssetDAOResponse>> getAllAssets (){
		List<AssetDAOResponse> assets = new ArrayList<AssetDAOResponse>();
		
		assets = assetSv.get(null);
		
		return new ResponseEntity<>(assets, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Asset by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No assets found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<AssetDAOResponse> getAssetsById (@PathVariable Long id){
		AssetDAOResponse asset = null;
		
		asset = assetSv.getById(id);
		
		return new ResponseEntity<>(asset, asset != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Connected Asset to a Asset with specified id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No assets found connected to this id")
	})
	@GetMapping(path = "/connect/{id}", produces = "application/json")
	public ResponseEntity<List<AssetDAOResponse>> getAssetsConnectedTo (@PathVariable Long id){
		List<AssetDAOResponse> assets = null;
		
		assets = assetSv.getConnectedTo(id);
		
		return new ResponseEntity<>(assets, assets != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Measurements from Asset with specified id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No measurements found to this asset id")
	})
	@GetMapping(path = "/measurement/{id}", produces = "application/json")
	public ResponseEntity<List<MeasurementDAOResponse>> getMeasurements (@PathVariable Long id){
		List<MeasurementDAOResponse> measurements = null;
		
		measurements = assetSv.getMeasurements(id);
		
		return new ResponseEntity<>(measurements, measurements != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== INFO REQUESTS ===================================================================================
		
	@Operation(summary = "Get Details from a Asset")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Assets havent details or doesn't exists")
	})
	@GetMapping(path = "{id}/info", produces = "application/json")
	public ResponseEntity<List<AssetDetails>> getInformationAsset (@PathVariable Long id){
		List<AssetDetails> info = null;
		
		info = assetSv.getDetailsByAssetId(id);
		
		info = info.isEmpty() ? null : info;
		
		return new ResponseEntity<List<AssetDetails>>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Insert Details for a Asset")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Details saved correctly"),
		@ApiResponse(responseCode = "500", description = "Error saving details")
	})
	@PostMapping(path = "{asset_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<AssetDetails> insertInformation (@RequestBody AssetDetails detail, @PathVariable Long asset_id){
		try {
			detail.setId(asset_id);
			AssetDetails _detail = assetSv.saveDetail(detail);
			return new ResponseEntity<>(_detail, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Update Information from its id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Details saved correctly"),
		@ApiResponse(responseCode = "400", description = "Path isn't valid"),
		@ApiResponse(responseCode = "404", description = "Detail not exist"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PutMapping(path = "{asset_id}/info/{info_id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<AssetDetails> updateInformation (@RequestBody AssetDetails detail, @PathVariable Long asset_id, @PathVariable Long info_id){
		try {
			detail.setId(asset_id);
			AssetDetails _detail = assetSv.updateDetail(detail, info_id);
			return new ResponseEntity<>(_detail, _detail != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Delete Information from its id")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Information delete"),
		@ApiResponse(responseCode = "500", description = "Error deleting information")
	})
	@DeleteMapping(path = "{asset_id}/info/{info_id}")
	public ResponseEntity<AssetDetails> deleteInformation (@PathVariable Long asset_id, @PathVariable Long info_id){
		try {
			assetSv.deleteById(info_id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
//=== POST REQUESTS ===================================================================================
	
	@Operation(summary = "Create a new Asset")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Asset saved correctly"),
			@ApiResponse(responseCode = "422", description = "Type isn't valid"),
			@ApiResponse(responseCode = "500", description = "Error saving asset")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<AssetDAOResponse> createAsset(@RequestBody AssetDAORequest asset) {
		try {
			if (Asset.ALLOWED_TYPES.keySet().stream().anyMatch(asset.getType()::equalsIgnoreCase)) {	
				AssetDAOResponse _asset = assetSv.save(asset);
				return new ResponseEntity<>(_asset, HttpStatus.CREATED);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			e.printStackTrace();
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
	public ResponseEntity<AssetDAOResponse> updateAsset(@RequestBody AssetDAORequest asset, @PathVariable Long id) {
		try {
			asset.setId(id);
			if (Asset.ALLOWED_TYPES.keySet().stream().anyMatch(asset.getType()::equalsIgnoreCase)) {	
				AssetDAOResponse _asset = assetSv.update(asset, id);
				return new ResponseEntity<>(_asset, _asset != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Connect two existing Asset")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Asset connected correctly"),
			@ApiResponse(responseCode = "404", description = "Asset can't be connected"),
			@ApiResponse(responseCode = "500", description = "Error connecting asset")
		}
	)
	@PutMapping(path = "/connect", produces = "application/json")
	public ResponseEntity<AssetDAOResponse> connectAssets(@RequestParam("asset_id") Long id, @RequestParam("connected_asset_id") Long connectId) {
		try {
			AssetDAOResponse _asset = assetSv.connect(id, connectId);
			return new ResponseEntity<>(_asset, _asset != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Add existing measurement to existing Asset")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Measurement added correctly"),
			@ApiResponse(responseCode = "404", description = "Asset not exists or one of more infrastructure asset connected to measurement"),
			@ApiResponse(responseCode = "500", description = "Error adding measurement")
		}
	)
	@PutMapping(path = "/measurement", produces = "application/json")
	public ResponseEntity<MeasurementDAOResponse> addMeasurement(@RequestParam("asset_id") Long assetId, @RequestParam("measurement_id") Long measurementId) {
		try {
			MeasurementDAOResponse _measurement = assetSv.addMeasurement(assetId, measurementId);
			return new ResponseEntity<>(_measurement, _measurement != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS ================================================================================
			
	@Operation(summary = "Delete a existing Asset", hidden = false)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Asset deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving asset")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteAsset(@PathVariable Long id) {
		try {
			assetSv.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
