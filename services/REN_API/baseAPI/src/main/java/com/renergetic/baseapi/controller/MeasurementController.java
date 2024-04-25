package com.renergetic.baseapi.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.renergetic.common.dao.MeasurementDAOImpl;
import com.renergetic.common.dao.details.MeasurementTagsDAO;
import com.renergetic.common.dao.details.TagDAO;
import com.renergetic.common.dao.MeasurementDAORequest;
import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.dao.ResourceDAO;

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
import com.renergetic.baseapi.service.MeasurementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Measurement Controller", description = "Allows add and see Measurements")
@RequestMapping("/api/measurements")
public class MeasurementController {

    @Autowired
    MeasurementService measurementSv;
    @Autowired
    MeasurementDetailsRepository informationRepository;

//=== GET REQUESTS====================================================================================

    @Operation(summary = "Get All Measurements having details key and value")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/key/{key}/value/{value}", produces = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> getTagMeasurements(
            @PathVariable(name = "key") String key,
            @PathVariable(name = "value") String value,
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {

        List<MeasurementDAOResponse> measurements =
                measurementSv.getByTag(key, value, offset.orElse(0L), limit.orElse(100));

        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }

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

        List<MeasurementDAOResponse> measurements;
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

    @Operation(summary = "Get All Measurements")
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
            @RequestParam(required = false, name = "tag_key") String tagKey,
            @RequestParam(required = false, name = "tag_value") String tagValue,
            @RequestParam(required = false, name = "type_id") Long typeId,
            @RequestParam(required = false, name = "type_physical_name") String physicalTypeName) {


        List<MeasurementDAOImpl> measurements;

        measurements = measurementSv.findMeasurements(name, domain, direction, sensorName,
                assetName, typeId, physicalTypeName, tagKey, tagValue, offset.orElse(0L), limit.orElse(1000));

        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }

    @Operation(summary = "Get linked panels for given measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/{id}/panels", produces = "application/json")
    public ResponseEntity<List<ResourceDAO>> listLinkedPanels(@PathVariable Long id) {
        List<ResourceDAO> l = measurementSv.listLinkedPanels(id);
        return new ResponseEntity<>(l, HttpStatus.OK);
    }

    @Operation(summary = "Get Measurement by id")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @ApiResponse(responseCode = "404", description = "No measurements found with this id")
    @GetMapping(path = {"{id}", "/id/{id}"}, produces = "application/json")
    public ResponseEntity<MeasurementDAOResponse> getMeasurementsById(@PathVariable Long id) {
        Measurement measurement = measurementSv.getById(id);
        MeasurementDAOResponse daoResponse = null;
        if (measurement != null)
            daoResponse = MeasurementDAOResponse.create(measurement, null, null);


        return new ResponseEntity<>(daoResponse,
                daoResponse != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Copy measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @ApiResponse(responseCode = "404", description = "No measurements found with this id")
    @PostMapping(path = {"/{id}/copy", "/id/{id}/copy"}, produces = "application/json")
    public ResponseEntity<MeasurementDAOResponse> duplicateMeasurement(@PathVariable Long id) {
        MeasurementDAOResponse daoResponse = measurementSv.duplicate(id);


        return new ResponseEntity<>(daoResponse,
                daoResponse != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get All Measurements Tags Keys")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/tags/key", produces = "application/json")
    public ResponseEntity<List<String>> getAllMeasurementTagKeys() {
        List<String> tags = measurementSv.getTagKeys();
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
        List<MeasurementType> type;

        type = measurementSv.getTypes(null, offset.orElse(0L), limit.orElse(20));

        return new ResponseEntity<>(type, HttpStatus.OK);
    }


    @Operation(summary = "Get Measurement Type by id")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @ApiResponse(responseCode = "404", description = "No measurement type found with this id")
    @GetMapping(path = "/type/{id}", produces = "application/json")
    public ResponseEntity<MeasurementType> getMeasurementsTypeById(@PathVariable Long id) {
        MeasurementType type = null;

        type = measurementSv.getTypeById(id);

        return new ResponseEntity<>(type, type != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get Measurement by id")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @ApiResponse(responseCode = "404", description = "No measurements found with this id")
    @GetMapping(path = {"/id/{id}/report"}, produces = "application/json")
    public ResponseEntity<MeasurementDAOImpl> getDetailedMeasurement(@PathVariable Long id) {
        MeasurementDAOImpl m = measurementSv.findMeasurement(id);
        m.setTags(measurementSv.getMeasurementTags(id));
        return new ResponseEntity<>(m, HttpStatus.OK);
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



//=== INFO REQUESTS ===================================================================================

    @Operation(summary = "Get Details from a Measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @ApiResponse(responseCode = "404", description = "Measurements havent details or doesn't exists")
    @GetMapping(path = "{measurement_id}/info", produces = "application/json")
    public ResponseEntity<List<MeasurementDetails>> getInformationMeasurement(@PathVariable("measurement_id") Long id) {
        List<MeasurementDetails> info = null;

        info = measurementSv.getDetailsByMeasurementId(id);

        info = info.isEmpty() ? null : info;

        return new ResponseEntity<>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get Details from a Measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @ApiResponse(responseCode = "404", description = "Measurements havent details or doesn't exists")
    @GetMapping(path = "{measurement_id}/properties", produces = "application/json")
    public ResponseEntity<Map<String, String>> getInformationMeasurementProperties(
            @PathVariable("measurement_id") Long id) {
        Map<String, String> info = new HashMap<>();

        measurementSv.getDetailsByMeasurementId(id).forEach(detail -> info.put(detail.getKey(), detail.getValue()));
        return new ResponseEntity<>(info.size() > 0 ? info : null, HttpStatus.OK);
    }

    @Operation(summary = "Insert measurement detail's property")
    @ApiResponse(responseCode = "200", description = "Details saved correctly")
    @ApiResponse(responseCode = "500", description = "Error saving details")
    @PostMapping(path = {"{measurement_id}/info", "/id/{measurement_id}/info"}, produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementDetails> saveProperty(@RequestBody MeasurementDetails detail,
                                                           @PathVariable("measurement_id") Long id) {
        Measurement measurement = new Measurement();
        measurement.setId(id);
        detail.setMeasurement(measurement);
        detail = measurementSv.saveDetail(detail);
        return new ResponseEntity<>(detail, HttpStatus.CREATED);
    }

    @Operation(summary = "Update measurement detail's property")
    @ApiResponse(responseCode = "200", description = "Details saved correctly")
    @ApiResponse(responseCode = "400", description = "Path isn't valid")
    @ApiResponse(responseCode = "404", description = "Detail not exist")
    @ApiResponse(responseCode = "500", description = "Error saving information")
    @PutMapping(path = {"{measurement_id}/info", "/id/{measurement_id}/info"}, produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementDetails> updateProperty(@RequestBody MeasurementDetails detail,
                                                             @PathVariable("measurement_id") Long id) {
        Measurement measurement = new Measurement();
        measurement.setId(id);
        detail.setMeasurement(measurement);
        return ResponseEntity.ok(measurementSv.updateDetail(detail));
    }

    @Operation(summary = "Delete Information from its id")
    @ApiResponse(responseCode = "204", description = "Information deleted")
    @ApiResponse(responseCode = "500", description = "Error deleting information")
    @DeleteMapping(path = {"{measurement_id}/info/{info_id}", "/id/{measurement_id}/info/{info_id}"})
    public ResponseEntity<MeasurementDetails> deleteInformation(@PathVariable("measurement_id") Long measurementId,
                                                                @PathVariable("info_id") Long infoId) {
        measurementSv.deleteDetailById(infoId);

        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Insert Details for Measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = {"{measurement_id}/info", "/id/{measurement_id}/info"}, produces = "application/json")
    public ResponseEntity<Boolean> setMeasurementProperty(
            @PathVariable(name = "measurement_id") Long measurementId,
            @RequestBody MeasurementDetails detail) {
        boolean res = measurementSv.setProperty(measurementId, detail);
        return new ResponseEntity<>(res, HttpStatus.OK);

    }

    @Operation(summary = "Insert Details for Measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = {"{measurement_id}/properties", "/id/{measurement_id}/properties"}, produces = "application/json")
    public ResponseEntity<Boolean> setMeasurementProperties(
            @PathVariable(name = "measurement_id") Long measurementId,
            @RequestBody Map<String, String> properties) {

        boolean res = measurementSv.setProperties(measurementId, properties);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    //=== TAGS REQUESTS ===================================================================================


//=== POST REQUESTS ===================================================================================

    @Operation(summary = "Create a new Measurement")
    @ApiResponse(responseCode = "201", description = "Measurement saved correctly")
    @ApiResponse(responseCode = "422", description = "Type isn't valid")
    @ApiResponse(responseCode = "500", description = "Error saving measurement")
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementDAOResponse> createMeasurement(@RequestBody MeasurementDAORequest measurement) {
        MeasurementDAOResponse measurementResponse = measurementSv.save(measurement);
        return new ResponseEntity<>(measurementResponse, HttpStatus.CREATED);
    }
    @Operation(summary = "Create a new Measurements")
    @ApiResponse(responseCode = "201", description = "Measurements saved correctly")
    @ApiResponse(responseCode = "422", description = "Type isn't valid")
    @ApiResponse(responseCode = "500", description = "Error saving measurement")
    @PostMapping(path = "/batch", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<MeasurementDAOResponse>> createMeasurements(@RequestBody List<MeasurementDAORequest> measurements) {
        List<MeasurementDAOResponse> measurementResponses = measurementSv.save(measurements);
        return new ResponseEntity<>(measurementResponses, HttpStatus.CREATED);
    }

    @Operation(summary = "Create a new Measurement Type")
    @ApiResponse(responseCode = "201", description = "Measurement Type saved correctly")
    @ApiResponse(responseCode = "422", description = "Type isn't valid")
    @ApiResponse(responseCode = "500", description = "Error saving measurement")
    @PostMapping(path = "/type", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementType> createMeasurementType(@RequestBody MeasurementType type) {
        type = measurementSv.saveType(type);
        return new ResponseEntity<>(type, HttpStatus.CREATED);
    }

    @Operation(summary = "Set dashboard visibility")
    @ApiResponse(responseCode = "201", description = "Measurement type updated")
    @ApiResponse(responseCode = "400", description = "Error saving Measurement type")
    @PostMapping(path = "/type/{id}/dashboard/{visibility}", produces = "application/json")
    public ResponseEntity<Boolean> setDashboardVisibility(@PathVariable("id") Long id,
                                                          @PathVariable("visibility") Boolean visibility) {
        Boolean currentFeatured = measurementSv.setDashboardVisibility(id, visibility);
        return new ResponseEntity<>(currentFeatured,
                currentFeatured.equals(visibility) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
//=== PUT REQUESTS====================================================================================

    @Operation(summary = "Update a existing Measurement")
    @ApiResponse(responseCode = "200", description = "Measurement saved correctly")
    @ApiResponse(responseCode = "404", description = "Measurement not exist")
    @ApiResponse(responseCode = "422", description = "Type isn's valid")
    @ApiResponse(responseCode = "500", description = "Error saving measurement")
    @PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementDAOResponse> updateMeasurement(@RequestBody MeasurementDAORequest measurement,
                                                                    @PathVariable Long id) {
        measurement.setId(id);
        MeasurementDAOResponse measurementResponse = measurementSv.update(measurement, id);
        return new ResponseEntity<>(measurementResponse, measurementResponse != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Update a existing Measurement Type")
    @ApiResponse(responseCode = "200", description = "Measurement Type saved correctly")
    @ApiResponse(responseCode = "404", description = "Measurement Type not exist")
    @ApiResponse(responseCode = "422", description = "Type isn's valid")
    @ApiResponse(responseCode = "500", description = "Error saving measurement type")
    @PutMapping(path = "/type/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementType> updateMeasurementType(@RequestBody MeasurementType type,
                                                                 @PathVariable Long id) {
        type.setId(id);
        type = measurementSv.updateType(type, id);
        return new ResponseEntity<>(type, type != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

//=== DELETE REQUESTS ================================================================================

    @Operation(summary = "Delete a existing Measurement", hidden = false)
    @ApiResponse(responseCode = "204", description = "Measurement deleted correctly")
    @ApiResponse(responseCode = "500", description = "Error saving measurement")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteMeasurement(@PathVariable Long id) {
        measurementSv.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a existing Measurement", hidden = false)
    @ApiResponse(responseCode = "204", description = "Measurement deleted correctly")
    @ApiResponse(responseCode = "500", description = "Error saving measurement")
    @DeleteMapping(path = "/type/{id}")
    public ResponseEntity<Void> deleteMeasurementType(@PathVariable Long id) {
        measurementSv.deleteTypeById(id);

        return ResponseEntity.noContent().build();
    }
    //region tags

    @Operation(summary = "Get All Tags")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "tags", produces = "application/json")
    public ResponseEntity<List<TagDAO>> getAllTags(@RequestParam(required = false) Optional<Long> offset,
                                                   @RequestParam(required = false) Optional<Integer> limit) {
        List<TagDAO> tags;
        tags = measurementSv.getTagsFilter(null, offset.orElse(0L), limit.orElse(500));
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @Operation(summary = "Get measurement tags")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "{id}/tags", produces = "application/json")
    public ResponseEntity<List<MeasurementTagsDAO>> getAllTags(@PathVariable Long id) {
        List<MeasurementTagsDAO> tags;

        tags = measurementSv.getMeasurementTags(id);

        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @Operation(summary = "Insert tags")
    @ApiResponse(responseCode = "200", description = "Tag saved correctly")
    @ApiResponse(responseCode = "500", description = "Error saving tag")
    @PostMapping(path = "tags", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementTags> insertTag(@RequestBody MeasurementTags tag) {
        tag = measurementSv.saveTag(tag);
        return new ResponseEntity<>(tag, HttpStatus.CREATED);
    }

    @Operation(summary = "Get Measurement Timeseries from InfluxDB")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @ApiResponse(responseCode = "404", description = "No measurement values found with this id")
    @GetMapping(path = "/{measurement_id}/values", produces = "application/json")
    public ResponseEntity<String> getMeasurementsValues(@PathVariable("measurement_id") Long measurementId,
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

    @Operation(summary = "Get All Measurement Tags by a key")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "tags/key/{key}", produces = "application/json")
    public ResponseEntity<List<TagDAO>> getAllTagsByKey(@PathVariable("key") String key,
                                                        @RequestParam(required = false) Optional<Long> offset,
                                                        @RequestParam(required = false) Optional<Integer> limit) {
        List<TagDAO> tags = measurementSv.getTags(key, offset.orElse(0L), limit.orElse(500));
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @Operation(summary = "Insert Tags for Measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = "{id}/tags", produces = "application/json")
    public ResponseEntity<Boolean> setTags(
            @PathVariable(name = "id") Long measurementId,
            @RequestBody Map<String, String> tags) {

        boolean res = measurementSv.setTags(measurementId, tags);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
    @Operation(summary = "Add tag to the measurement")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = "/id/{id}/tags/key/{key}/value/{value}", produces = "application/json")
    public ResponseEntity<Boolean> setMeasurementTag(
            @PathVariable(name = "id") Long measurementId, @PathVariable String key, @PathVariable String value) {
        boolean res = measurementSv.setTag(measurementId, key, value);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Delete tag")
    @ApiResponse(responseCode = "204", description = "Tag deleted")
    @ApiResponse(responseCode = "500", description = "Error deleting tag")
    @DeleteMapping(path = "tags/key/{key}/value/{value}")
    public ResponseEntity<Boolean> deleteTag(@PathVariable String key, @PathVariable String value) {
        measurementSv.deleteTag(key, value);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }


    @Operation(summary = "Update tags")
    @ApiResponse(responseCode = "200", description = "Tag saved correctly")
    @ApiResponse(responseCode = "400", description = "Path isn't valid")
    @ApiResponse(responseCode = "404", description = "Tag not exist")
    @ApiResponse(responseCode = "500", description = "Error saving information")
    @PutMapping(path = "tags/{tag_id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MeasurementTags> updateTag(@RequestBody MeasurementTags tag, @PathVariable("tag_id") Long tagId) {
        tag = measurementSv.updateTag(tag, tagId);
        return new ResponseEntity<>(tag, tag != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Delete tags")
    @ApiResponse(responseCode = "204", description = "Tag deleted")
    @ApiResponse(responseCode = "500", description = "Error deleting tag")
    @DeleteMapping(path = "tags/{tag_id}")
    public ResponseEntity<MeasurementDetails> deleteInformation(@PathVariable("tag_id") Long tagId) {
        measurementSv.deleteTagById(tagId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove tag from the measurement")
    @ApiResponse(responseCode = "204", description = "Tag deleted")
    @ApiResponse(responseCode = "500", description = "Error deleting tag")
    @DeleteMapping(path = "/id/{id}/tags/key/{key}")
    public ResponseEntity<Boolean> deleteMeasurementTag(@PathVariable Long id, @PathVariable String key) {
        measurementSv.deleteMeasurementTag(id, key);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
    //endregion
}
