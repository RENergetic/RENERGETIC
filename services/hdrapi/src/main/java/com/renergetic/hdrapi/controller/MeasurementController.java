package com.renergetic.hdrapi.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.renergetic.common.dao.*;
import com.renergetic.hdrapi.dao.MeasurementDAOImpl;
import com.renergetic.hdrapi.dao.details.MeasurementTagsDAO;
import com.renergetic.hdrapi.dao.details.TagDAO;
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
import org.springframework.web.client.RestTemplate;

import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.information.MeasurementDetailsRepository;
import com.renergetic.hdrapi.service.MeasurementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.websocket.server.PathParam;

@Deprecated // SEE BASE API
@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Measurement Controller", description = "Allows add and see Measurements")
@RequestMapping("/api/measurements")
public class MeasurementController {
    //#region fields
    @Autowired
    MeasurementService measurementSv;
    @Autowired
    MeasurementDetailsRepository informationRepository;

    //#endregion
    @Operation(summary = "Get All Measurements")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> getAllMeasurements(
            @RequestParam(required = false) Optional<String> name,
            @RequestParam(required = false) Optional<String> type,
            @RequestParam(required = false) Optional<String> direction,
            @RequestParam(required = false) Optional<String> domain,
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {

        List<MeasurementDAOResponse> measurements = new ArrayList<>();
        HashMap<String, String> filters = new HashMap<>();
        if (name.isPresent()) filters.put("name", name.get());
        if (type.isPresent()) filters.put("type", type.get());
        if (direction.isPresent()) filters.put("direction", direction.get());
        if (domain.isPresent()) filters.put("domain", domain.get());
        if (filters.size() == 1 && filters.containsKey("name")) {
            measurements = measurementSv.find(filters, offset.orElse(0L), limit.orElse(20));
        } else {
            measurements = measurementSv.get(filters, offset.orElse(0L), limit.orElse(20)); //-> old method
        }
        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }

    //#region GET: measurements
    @Operation(summary = "List Measurements")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/report", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOImpl>> listMeasurements(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false, name = "sensor_name") String sensorName,
            @RequestParam(required = false, name = "asset_name") String assetName,
            @RequestParam(required = false, name = "asset_id") Long assetId,
            @RequestParam(required = false, name = "tag_key") String tagKey,
            @RequestParam(required = false, name = "tag_value") String tagValue,
            @RequestParam(required = false, name = "type_id") Long typeId,
            @RequestParam(required = false, name = "type_physical_name") String physicalTypeName) {
        List<MeasurementDAOImpl> measurements =
                measurementSv.findMeasurements(name, domain, direction, sensorName, assetId,
                        assetName, typeId, physicalTypeName, tagKey, tagValue, offset.orElse(0L), limit.orElse(1000));

        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }

    @Operation(summary = "List basic(initial) measurement - measurement without extra tags.")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/basic", produces = "application/json")
    public ResponseEntity<MeasurementDAOImpl> listMeasurements(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false, name = "sensor_name") String sensorName,
            @RequestParam(required = false, name = "asset_id") Long assetId,
            @RequestParam(required = false, name = "tag_key") String tagKey,
            @RequestParam(required = false, name = "type_id") Long typeId,
            @RequestParam(required = false, name = "type_physical_name") String physicalTypeName) {

        List<MeasurementDAOImpl> measurements =
                measurementSv.findMeasurements(name, domain, direction, sensorName, assetId,
                        null, typeId, physicalTypeName, tagKey, null, offset.orElse(0L), limit.orElse(1000), true);
        if (measurements.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.OK);
        var m = measurements.get(0);
        m.setTags(measurementSv.getMeasurementTags(m.getId()));
        return new ResponseEntity<>(m, HttpStatus.OK);
    }

    @Operation(summary = "Get measurements by tag key and value")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/key/{key}/value/{value}", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> getMeasurementsByProperty(
            @PathVariable(name = "key") String key, @PathVariable(name = "value") String value,
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {
        return new ResponseEntity<>(
                measurementSv.getByTag(key, value, offset.orElse(0L), limit.orElse(100)), HttpStatus.OK);
    }

    @Operation(summary = "Get all asset measurements")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/report/asset/{id}", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOImpl>> listAssetDetailedMeasurements(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit,
            @PathVariable(required = true, name = "id") Long assetId) {
        List<MeasurementDAOImpl> measurements =
                measurementSv.findAssetMeasurements(assetId, offset.orElse(0L), limit.orElse(500));
        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }
    //endregion

    //#region GET: measurement details
    @Operation(summary = "Get measurement properties ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "Measurements doesn't have details or doesn't exists")
    })
    @GetMapping(path = {"/{id}/properties", "/id/{id}/properties"}, produces = "application/json")
    public ResponseEntity<Map<String, String>> getInformationMeasurementProperties(
            @PathVariable("id") Long id) {
        Map<String, String> info = new HashMap<>();

        measurementSv.getDetailsByMeasurementId(id).forEach(detail -> info.put(detail.getKey(), detail.getValue()));
        return new ResponseEntity<>(info.size() > 0 ? info : null, HttpStatus.OK);
    }

    @Operation(summary = "Get Measurement's Timeseries from InfluxDB")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No measurement values found with this id")
    })
    @GetMapping(path = {"/{id}/values", "/id/{id}/values"}, produces = "application/json")
    public ResponseEntity<String> getMeasurementsValues(@PathVariable("id") Long measurementId,
                                                        @RequestParam Map<String, String> tags) {
        String url = "http://backinflux-sv:8082/api/";
        url += measurementSv.getById(measurementId).getName();

        if (tags != null && tags.size() > 0)
            url += "?" + String.join("&",
                    tags.keySet().stream()
                            .map(key -> key + "=" + tags.get(key))
                            .collect(Collectors.toList()));

        RestTemplate apiInflux = new RestTemplate();
        String data = apiInflux.getForObject(url, String.class);

        return new ResponseEntity<>(data, data != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get linked information panels/dashboards with the measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = {"/{id}/panels", "/id/{id}/panels"}, produces = "application/json")
    public ResponseEntity<List<ResourceDAO>> listLinkedPanels(@PathVariable Long id) {
        List<ResourceDAO> l = measurementSv.listLinkedPanels(id);
        return new ResponseEntity<>(l, HttpStatus.OK);
    }

    @Operation(summary = "Get measurement's tags")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = {"{id}/tags", "/id/{id}/tags"}, produces = "application/json")
    public ResponseEntity<List<MeasurementTagsDAO>> getAllTags(@PathVariable Long id) {
        List<MeasurementTagsDAO> tags = measurementSv.getMeasurementTags(id);
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @Operation(summary = "Get Measurement by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No measurements found with this id")
    })
    @GetMapping(path = {"/id/{id}/report"}, produces = "application/json")
    public ResponseEntity<MeasurementDAOImpl> getDetailedMeasurement(@PathVariable Long id) {
        var m = measurementSv.findMeasurement(id);
        m.setTags(measurementSv.getMeasurementTags(id));
        return new ResponseEntity<>(m, HttpStatus.OK);
    }

    @Operation(summary = "Get measurement by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No measurements found with this id")
    })
    @GetMapping(path = {"{id}", "/id/{id}"}, produces = "application/json")
    public ResponseEntity<MeasurementDAOResponse> getMeasurementsById(@PathVariable Long id) {
        var measurement = measurementSv.getById(id);
        MeasurementDAOResponse daoResponse = null;
        if (measurement != null)
            daoResponse = MeasurementDAOResponse.create(measurement, null, null);
        return new ResponseEntity<>(daoResponse,
                daoResponse != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
    //#endregion

    //#region UPDATE: measurement

    @Operation(summary = "Create a new Measurement")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Measurement saved correctly"),
            @ApiResponse(responseCode = "422", description = "Type isn't valid"),
            @ApiResponse(responseCode = "500", description = "Error saving measurement")})
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementDAOResponse> createMeasurement(@RequestBody MeasurementDAORequest measurement) {
        MeasurementDAOResponse _measurement = measurementSv.save(measurement);
        return new ResponseEntity<>(_measurement, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a existing Measurement", hidden = false)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Measurement deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving measurement")
    })
    @DeleteMapping(path = {"/{id}", "/id/{id}"})
    public ResponseEntity<?> deleteMeasurement(@PathVariable Long id) {
        measurementSv.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update an existing Measurement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Measurement saved correctly"),
            @ApiResponse(responseCode = "404", description = "Measurement not exist"),
            @ApiResponse(responseCode = "422", description = "Type isn's valid"),
            @ApiResponse(responseCode = "500", description = "Error saving measurement")
    }
    )
    @PutMapping(path = {"/{id}", "/id/{id}"}, produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementDAOResponse> updateMeasurement(@RequestBody MeasurementDAORequest measurement,
                                                                    @PathVariable Long id) {
        measurement.setId(id);
        MeasurementDAOResponse _measurement = measurementSv.update(measurement, id);
        return new ResponseEntity<>(_measurement, _measurement != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Upload multiple measurements")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Measurements saved correctly"),
            @ApiResponse(responseCode = "422", description = "Type isn't valid"),
            @ApiResponse(responseCode = "500", description = "Error saving measurement")})
    @PostMapping(path = "/batch", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> createMeasurements(
            @RequestBody List<MeasurementDAORequest> measurements) {
        List<MeasurementDAOResponse> _measurements = measurementSv.save(measurements);
        return new ResponseEntity<>(_measurements, HttpStatus.CREATED);
    }

    @Operation(summary = "Copy measurement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No measurements found with this id")
    })
    @PostMapping(path = {"/{id}/copy", "/id/{id}/copy"}, produces = "application/json")
    public ResponseEntity<MeasurementDAOResponse> duplicateMeasurement(@PathVariable Long id) {
        var daoResponse = measurementSv.duplicate(id);
        return new ResponseEntity<>(daoResponse,
                daoResponse != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Update measurement detail's property")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Details saved correctly"),
            @ApiResponse(responseCode = "400", description = "Path isn't valid"),
            @ApiResponse(responseCode = "404", description = "Detail not exist"),
            @ApiResponse(responseCode = "500", description = "Error saving information")
    })
    @PutMapping(path = "/id/{id}/info", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementDetails> updateProperty(@RequestBody MeasurementDetails detail,
                                                             @PathVariable("id") Long id) {
        Measurement measurement = new Measurement();
        measurement.setId(id);
        detail.setMeasurement(measurement);
        return ResponseEntity.ok(measurementSv.updateDetail(detail));
    }

    @Operation(summary = "Set measurement properties")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = {"/{id}/properties", "/id/{id}/properties"}, produces = "application/json")
    public ResponseEntity<Boolean> setMeasurementProperties(
            @PathVariable(name = "id") Long measurementId,
            @RequestBody Map<String, String> properties) {
//TODO check privileges
        boolean res = measurementSv.setProperties(measurementId, properties);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @Operation(summary = "Set measurement's tags")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = {"/{id}/tags", "/id/{id}/tags"}, produces = "application/json")
    public ResponseEntity<Boolean> setTags(
            @PathVariable(name = "id") Long measurementId,
            @RequestBody Map<String, String> tags) {
//TODO check privileges
        boolean res = measurementSv.setTags(measurementId, tags);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @Operation(summary = "Add tag to the measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = "/id/{id}/tags/key/{key}/value/{value}", produces = "application/json")
    public ResponseEntity<Boolean> setMeasurementTag(
            @PathVariable(name = "id") Long measurementId, @PathVariable String key, @PathVariable String value) {
//TODO check privileges
        boolean res = measurementSv.setTag(measurementId, key, value);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Remove tag from the measurement")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag deleted"),
            @ApiResponse(responseCode = "500", description = "Error deleting tag")
    })
    @DeleteMapping(path = "/id/{id}/tags/key/{key}")
    public ResponseEntity<Boolean> deleteMeasurementTag(@PathVariable Long id, @PathVariable String key) {
        measurementSv.deleteMeasurementTag(id, key);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
    //#endregion

    //#region GET: tags, measurement types
    @Operation(summary = "Get All Measurement Tags Keys")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/tags/key", produces = "application/json")
    public ResponseEntity<List<String>> getAllMeasurementTagKeys() {
        List<String> tags = measurementSv.getTagKeys();
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @Operation(summary = "Get All Measurement Tags by a key")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "tags/key/{key}", produces = "application/json")
    public ResponseEntity<List<TagDAO>> getAllTagsByKey(@PathVariable("key") String key,
                                                        @RequestParam(required = false) Optional<Long> offset,
                                                        @RequestParam(required = false) Optional<Integer> limit) {
        List<TagDAO> tags = measurementSv.getTags(key, offset.orElse(0L), limit.orElse(500));
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @Operation(summary = "Get All Tags")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "tags", produces = "application/json")
    public ResponseEntity<List<TagDAO>> getAllTags(@RequestParam(required = false) Optional<Long> offset,
                                                   @RequestParam(required = false) Optional<Integer> limit) {
        List<TagDAO> tags = measurementSv.getTags(null, offset.orElse(0L), limit.orElse(500));
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @Operation(summary = "Get All Measurements Tags Keys")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/tags/key/{key}/values", produces = "application/json")
    public ResponseEntity<List<String>> getAllMeasurementTagKeys(@PathVariable String key) {
        List<String> tagValues = measurementSv.getTagValues(key);
        return new ResponseEntity<>(tagValues, HttpStatus.OK);
    }

    @Operation(summary = "Get All Measurements Types")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/type", produces = "application/json")
    public ResponseEntity<List<MeasurementType>> getAllMeasurementsTypes(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {
        List<MeasurementType> type = new ArrayList<>();

        type = measurementSv.getTypes(null, offset.orElse(0L), limit.orElse(20));

        return new ResponseEntity<>(type, HttpStatus.OK);
    }

    @Operation(summary = "Get Measurement Type by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No measurement type found with this id")
    })
    @GetMapping(path = "/type/{id}", produces = "application/json")
    public ResponseEntity<MeasurementType> getMeasurementsTypeById(@PathVariable Long id) {
        MeasurementType type = null;

        type = measurementSv.getTypeById(id);

        return new ResponseEntity<>(type, type != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
    //#endregion

    //#region UPDATE: tags, measurement types
    @Operation(summary = "Create a new Measurement Type")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Measurement Type saved correctly"),
            @ApiResponse(responseCode = "422", description = "Type isn't valid"),
            @ApiResponse(responseCode = "500", description = "Error saving measurement")
    }
    )
    @PostMapping(path = "/type", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementType> createMeasurementType(@RequestBody MeasurementType type) {
        MeasurementType _type = measurementSv.saveType(type);
        return new ResponseEntity<>(_type, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete an existing measurement type", hidden = false)
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Measurement deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving measurement")})
    @DeleteMapping(path = "/type/{id}")
    public ResponseEntity<?> deleteMeasurementType(@PathVariable Long id) {
        measurementSv.deleteTypeById(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<MeasurementType> updateMeasurementType(@RequestBody MeasurementType type,
                                                                 @PathVariable Long id) {
        type.setId(id);
        MeasurementType _type = measurementSv.updateType(type, id);
        return new ResponseEntity<>(_type, _type != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Set measurement type visibility in the grafana dashboards")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Measurement type updated"),
            @ApiResponse(responseCode = "400", description = "Error saving Measurement type")
    })
    @PostMapping(path = "/type/{id}/dashboard/{visibility}", produces = "application/json")
    public ResponseEntity<Boolean> setDashboardVisibility(@PathVariable("id") Long id,
                                                          @PathVariable("visibility") Boolean visibility) {
//todo check privileges
        var currentFeatured = measurementSv.setDashboardVisibility(id, visibility);
        return new ResponseEntity<>(currentFeatured,
                currentFeatured == visibility ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Create new tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving tag")
    })
    @PostMapping(path = "tags", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementTags> insertTag(@RequestBody MeasurementTags tag) {
        MeasurementTags _tag = measurementSv.saveTag(tag);
        return new ResponseEntity<>(_tag, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete tag")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag deleted"),
            @ApiResponse(responseCode = "500", description = "Error deleting tag")
    })
    @DeleteMapping(path = "tags/key/{key}/value/{value}")
    public ResponseEntity<Boolean> deleteTag(@PathVariable String key, @PathVariable String value) {
        measurementSv.deleteTag(key, value);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
    //#endregion


    //#region TODO: is it used somewhere ? we could clear some of those methods
    @Operation(summary = "Update tags")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag saved correctly"),
            @ApiResponse(responseCode = "400", description = "Path isn't valid"),
            @ApiResponse(responseCode = "404", description = "Tag not exist"),
            @ApiResponse(responseCode = "500", description = "Error saving information")
    })
    @PutMapping(path = "tags/{tag_id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementTags> updateTag(@RequestBody MeasurementTags tag, @PathVariable Long tag_id) {
        MeasurementTags _tag = measurementSv.updateTag(tag, tag_id);
        return new ResponseEntity<>(_tag, _tag != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Delete tags")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag deleted"),
            @ApiResponse(responseCode = "500", description = "Error deleting tag")
    })
    @DeleteMapping(path = "tags/{tag_id}")
    public ResponseEntity<MeasurementDetails> deleteInformation(@PathVariable Long tag_id) {
        measurementSv.deleteTagById(tag_id);

        return ResponseEntity.noContent().build();
    }

    //TODO: is it used somewhere ? if not we can delete it
    @Operation(summary = "Get Details from a Measurement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "Measurements havent details or doesn't exists")
    })
    @GetMapping(path = "{measurement_id}/info", produces = "application/json")
    public ResponseEntity<List<MeasurementDetails>> getInformationMeasurement(@PathVariable("measurement_id") Long id) {
        List<MeasurementDetails> info = null;

        info = measurementSv.getDetailsByMeasurementId(id);

        info = info.isEmpty() ? null : info;

        return new ResponseEntity<List<MeasurementDetails>>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Insert measurement detail's property")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Details saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving details")
    })
    //TODO: is it used somewhere ? if not we can delete it
    @PostMapping(path = "{measurement_id}/info", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementDetails> saveProperty(@RequestBody MeasurementDetails detail,
                                                           @PathVariable("measurement_id") Long id) {
        Measurement measurement = new Measurement();
        measurement.setId(id);
        detail.setMeasurement(measurement);
        MeasurementDetails _detail = measurementSv.saveDetail(detail);
        return new ResponseEntity<>(_detail, HttpStatus.CREATED);
    }


    //TODO: is it used somewhere ? if not we can delete it
    @Operation(summary = "Insert Details for Measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "{measurement_id}/info", produces = "application/json")
    public ResponseEntity<Boolean> setMeasurementProperty(
            @PathVariable(name = "measurement_id") Long measurementId,
            @RequestBody MeasurementDetails detail) {
//TODO check privileges
        boolean res = measurementSv.setProperty(measurementId, detail);
        return new ResponseEntity<>(res, HttpStatus.OK);

    }


    //TODO: is it used somewhere ? if not we can delete it
    @Operation(summary = "Delete Information from its id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Information deleted"),
            @ApiResponse(responseCode = "500", description = "Error deleting information")
    })
    @DeleteMapping(path = "{measurement_id}/info/{info_id}")
    public ResponseEntity<MeasurementDetails> deleteInformation(@PathVariable Long measurement_id,
                                                                @PathVariable Long info_id) {
        measurementSv.deleteDetailById(info_id);

        return ResponseEntity.noContent().build();
    }


    //#endregion

}
