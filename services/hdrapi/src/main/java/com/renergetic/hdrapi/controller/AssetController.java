package com.renergetic.hdrapi.controller;

import com.renergetic.common.dao.AssetCategoryDAO;
import com.renergetic.common.dao.AssetConnectionDAORequest;
import com.renergetic.common.dao.AssetDAORequest;
import com.renergetic.common.dao.AssetDAOResponse;
import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.AssetType;
import com.renergetic.common.model.ConnectionType;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.information.AssetDetailsRepository;
import com.renergetic.hdrapi.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Asset Controller", description = "Allows add and see Assets")
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    AssetService assetSv;
    @Autowired
    AssetDetailsRepository informationRepository;
    @Autowired
    AssetService assetService;

//=== GET REQUESTS====================================================================================

    @Operation(summary = "Get All Assets")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<AssetDAOResponse>> getAllAssets(@RequestParam(required = false) Optional<Long> offset,
                                                               @RequestParam(required = false) Optional<Integer> limit,
                                                               @RequestParam(required = false) Optional<String> category,
                                                               @RequestParam(required = false) Optional<String> type,
                                                               @RequestParam(required = false) Optional<String> name,
                                                               @RequestParam(required = false) Optional<String> label,
                                                               @RequestParam(required = false) Optional<Long> owner_id,
                                                               @RequestParam(required = false) Optional<Long> parent_id) {
        List<AssetDAOResponse> assets = new ArrayList<AssetDAOResponse>();
        HashMap<String, String> filters = new HashMap<>();

        if (category.isPresent() && !category.get().isEmpty()) filters.put("category", category.get().toString());
        if (type.isPresent() && !type.get().isEmpty()) filters.put("type", type.get());
        if (name.isPresent() && name.get().length() > 2) filters.put("name", name.get());
        if (label.isPresent() && label.get().length() > 2) filters.put("label", label.get());
        owner_id.ifPresent(aLong -> filters.put("owner", aLong.toString()));
        parent_id.ifPresent(aLong -> filters.put("parent", aLong.toString()));

        try {
            assets = assetSv.get(filters, offset.orElse(0L), limit.orElse(20));
        } catch (NotFoundException ex) {
            assets = Collections.emptyList();
        }


        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

    @Operation(summary = "Get Asset by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No assets found with this id")
    })
    @GetMapping(path = "{id}", produces = "application/json")
    public ResponseEntity<AssetDAOResponse> getAssetsById(@PathVariable Long id) {
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
    public ResponseEntity<List<AssetDAOResponse>> getAssetsConnectedTo(@PathVariable Long id) {
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
    public ResponseEntity<List<MeasurementDAOResponse>> getMeasurements(@PathVariable Long id) {
        List<MeasurementDAOResponse> measurements = null;

        measurements = assetSv.getMeasurements(id);

        return new ResponseEntity<>(measurements, measurements != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get All Assets Types")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/type", produces = "application/json")
    public ResponseEntity<List<AssetType>> getAllMeasurementsTypes(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {
        List<AssetType> type = new ArrayList<>();

        type = assetSv.getTypes(null, offset.orElse(0L), limit.orElse(20));

        return new ResponseEntity<>(type, HttpStatus.OK);
    }

    @Operation(summary = "Get Asset Type by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No asset type found with this id")
    })
    @GetMapping(path = "/type/{id}", produces = "application/json")
    public ResponseEntity<AssetType> getAssetsTypeById(@PathVariable Long id) {
        AssetType type = null;

        type = assetSv.getTypeById(id);

        return new ResponseEntity<>(type, type != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

//=== INFO REQUESTS ===================================================================================

    @Operation(summary = "Get Details from a Asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "Assets havent details or doesn't exists")
    })
    @GetMapping(path = "{asset_id}/info", produces = "application/json")
    public ResponseEntity<List<AssetDetails>> getInformationAsset(@PathVariable("asset_id") Long assetId) {
        List<AssetDetails> info = null;

        info = assetSv.getDetailsByAssetId(assetId);

        return new ResponseEntity<List<AssetDetails>>(info, HttpStatus.OK);
    }

    @Operation(summary = "Insert Details for a Asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Details saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving details")
    })
    @PostMapping(path = "{asset_id}/info", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AssetDetails> insertInformation(@RequestBody AssetDetails detail,
                                                          @PathVariable("asset_id") Long assetId) {
        AssetDetails _detail = assetSv.saveDetail(detail, assetId);
        return new ResponseEntity<>(_detail, HttpStatus.CREATED);
    }

    @Operation(summary = "Update Information from its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Details saved correctly"),
            @ApiResponse(responseCode = "400", description = "Path isn't valid"),
            @ApiResponse(responseCode = "404", description = "Detail not exist"),
            @ApiResponse(responseCode = "500", description = "Error saving information")
    })
    @PutMapping(path = "{asset_id}/info/{info_id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AssetDetails> updateInformation(@RequestBody AssetDetails detail,
                                                          @PathVariable("asset_id") Long assetId,
                                                          @PathVariable("info_id") Long infoId) {
        detail.setId(infoId);
        AssetDetails _detail = assetSv.updateDetail(detail, infoId, assetId);
        return new ResponseEntity<>(_detail, _detail != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Update Information from its key")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Details saved correctly"),
            @ApiResponse(responseCode = "400", description = "Path isn't valid"),
            @ApiResponse(responseCode = "404", description = "Detail not exist"),
            @ApiResponse(responseCode = "500", description = "Error saving information")
    })
    @PutMapping(path = "{asset_id}/info", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AssetDetails> updateInformationByKey(@RequestBody AssetDetails detail,
                                                               @PathVariable("asset_id") Long assetId) {
        AssetDetails _detail = assetSv.updateDetailByKey(detail, assetId);
        return new ResponseEntity<>(_detail, _detail != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Delete Information from its id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Information delete"),
            @ApiResponse(responseCode = "500", description = "Error deleting information")
    })
    @DeleteMapping(path = "{asset_id}/info/{info_id}")
    public ResponseEntity<AssetDetails> deleteInformation(@PathVariable("asset_id") Long assetId,
                                                          @PathVariable("info_id") Long infoId) {
        assetSv.deleteDetailById(infoId, assetId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete Information from its key")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Information delete"),
            @ApiResponse(responseCode = "500", description = "Error deleting information")
    })
    @DeleteMapping(path = "{asset_id}/info/key/{key}")
    public ResponseEntity<AssetDetails> deleteInformationByKey(@PathVariable("asset_id") Long assetId,
                                                               @PathVariable("key") String key) {
        assetSv.deleteDetailByKey(key, assetId);

        return ResponseEntity.noContent().build();
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
        AssetDAOResponse _asset = assetSv.save(asset);
        return new ResponseEntity<>(_asset, HttpStatus.CREATED);
    }

    @Operation(summary = "Create a new Asset Type")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Measurement Type saved correctly"),
            @ApiResponse(responseCode = "422", description = "Type isn't valid"),
            @ApiResponse(responseCode = "500", description = "Error saving measurement")
    }
    )
    @PostMapping(path = "/type", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AssetType> createAssetType(@RequestBody AssetType type) {
        System.err.println(type);
        AssetType _type = assetSv.saveType(type);
        return new ResponseEntity<>(_type, HttpStatus.CREATED);
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
        asset.setId(id);
        AssetDAOResponse _asset = assetSv.update(asset, id);
        return new ResponseEntity<>(_asset, _asset != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Assign parent to an existing Asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset saved correctly"),
            @ApiResponse(responseCode = "404", description = "Asset not exist"),
            @ApiResponse(responseCode = "422", description = "Type isn's valid"),
            @ApiResponse(responseCode = "500", description = "Error assigning parent")
    }
    )
    @PutMapping(path = "/{id}/parent/{parentId}" )
    public ResponseEntity  assignParent(@PathVariable Long id, @PathVariable Long parentId) {
        assetSv.assignParent(id, parentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Operation(summary = "Delete parent from  an existing Asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset saved correctly"),
            @ApiResponse(responseCode = "404", description = "Asset not exist"),
            @ApiResponse(responseCode = "422", description = "Type isn's valid"),
            @ApiResponse(responseCode = "500", description = "Error revoking parent")
    }
    )
    @DeleteMapping(path = "/{id}/parent" )
    public ResponseEntity revokeParent(@PathVariable Long id) {
        assetSv.revokeParent(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Connect two existing Asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset connected correctly"),
            @ApiResponse(responseCode = "404", description = "Asset can't be connected"),
            @ApiResponse(responseCode = "500", description = "Error connecting asset")
    }
    )
    @PutMapping(path = "/connect/{id}", produces = "application/json")
    public ResponseEntity<AssetDAOResponse> connectAssets(@PathVariable Long id,
                                                          @RequestParam("connect_to") Long connectId,
                                                          @RequestParam(value = "type", required = false) Optional<ConnectionType> type) {
        AssetConnectionDAORequest connection = new AssetConnectionDAORequest();
        connection.setAssetId(id);
        connection.setAssetConnectedId(connectId);
        connection.setType(type.orElse(null));
        AssetDAOResponse _asset = assetSv.connect(connection);
        return new ResponseEntity<>(_asset, _asset != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Add existing measurement to existing Asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Measurement added correctly"),
            @ApiResponse(responseCode = "404", description = "Asset not exists or one of more infrastructure asset connected to measurement"),
            @ApiResponse(responseCode = "500", description = "Error adding measurement")
    }
    )
    @PutMapping(path = "/measurement", produces = "application/json")
    public ResponseEntity<MeasurementDAOResponse> addMeasurement(@RequestParam("asset_id") Long assetId,
                                                                 @RequestParam("measurement_id") Long measurementId) {
        MeasurementDAOResponse _measurement = assetSv.assignMeasurement(assetId, measurementId);
        return new ResponseEntity<>(_measurement, _measurement != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Add existing measurement to existing Asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Measurement connected to asset correctly"),
            @ApiResponse(responseCode = "404", description = "Asset or measurement does not exist"),
            @ApiResponse(responseCode = "500", description = "Error connecting measurement")
    }
    )
    @PutMapping(path = "{assetId}/measurement/{measurementId}", produces = "application/json")
    public ResponseEntity<MeasurementDAOResponse> assignMeasurement(@PathVariable Long assetId,
                                                                    @PathVariable Long measurementId) {
        MeasurementDAOResponse _measurement = assetSv.assignMeasurement(assetId, measurementId);
        return new ResponseEntity<>(_measurement, _measurement != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Add existing measurement to existing Asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Measurement connected to asset correctly"),
            @ApiResponse(responseCode = "404", description = "Asset or measurement does not exist"),
            @ApiResponse(responseCode = "500", description = "Error connecting measurement")
    }
    )
    @DeleteMapping(path = "{assetId}/measurement/{measurementId}", produces = "application/json")
    public ResponseEntity<MeasurementDAOResponse> revokeMeasurement(@PathVariable Long assetId,
                                                                    @PathVariable Long measurementId) {
        MeasurementDAOResponse _measurement = assetSv.revokeMeasurement(assetId, measurementId);
        return new ResponseEntity<>(_measurement, _measurement != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Update a existing Measurement Type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Measurement Type saved correctly"),
            @ApiResponse(responseCode = "404", description = "Measurement Type not exist"),
            @ApiResponse(responseCode = "422", description = "Type isn's valid"),
            @ApiResponse(responseCode = "500", description = "Error saving measurement type")
    }
    )
    @PutMapping(path = "/type/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AssetType> updateAssetType(@RequestBody AssetType type, @PathVariable Long id) {
        type.setId(id);
        AssetType _type = assetSv.updateType(type, id);
        return new ResponseEntity<>(_type, _type != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Connect an existing asset to an asset category", hidden = false)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset category correctly updated"),
            @ApiResponse(responseCode = "404", description = "Asset not exists "),
            @ApiResponse(responseCode = "500", description = "Error saving measurement")
    }
    )
    @PutMapping(path = "/{id}/category")
    public ResponseEntity<AssetDAOResponse> connectAssetToCategory(@PathVariable Long id,
                                                                   @RequestBody AssetCategoryDAO connectCategory) {
        AssetDAOResponse assetDAOupdate = assetService.updateAssetCategory(connectCategory, id);
        return new ResponseEntity<>(assetDAOupdate, HttpStatus.OK);
    }

    //=== DELETE REQUESTS ================================================================================
    @DeleteMapping(path = "/connect/{id}", produces = "application/json")
    public ResponseEntity<?> connectAssets(@PathVariable Long id, @RequestParam("connected_asset_id") Long connectId,
                                           @RequestParam("type") ConnectionType connectionType) {
        AssetConnectionDAORequest connection = new AssetConnectionDAORequest();
        connection.setAssetConnectedId(connectId);
        connection.setAssetId(id);
        connection.setType(connectionType);
        assetService.disconnect(connection);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a existing Asset", hidden = false)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Asset deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving asset")
    }
    )
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Long id) {
        assetSv.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a existing Measurement", hidden = false)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Measurement deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving measurement")
    }
    )
    @DeleteMapping(path = "/type/{id}")
    public ResponseEntity<?> deleteAssetType(@PathVariable Long id) {
        assetSv.deleteTypeById(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a connection between an asset an its category", hidden = false)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Connection deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting the connection")
    }
    )
    @DeleteMapping(path = "/{id}/category")
    public ResponseEntity<?> deleteConnectionAssetToCategory(@PathVariable Long id) {
        assetService.deleteAssetCategory(id);
        return ResponseEntity.noContent().build();
    }
}
