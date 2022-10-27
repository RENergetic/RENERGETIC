package com.renergetic.hdrapi.controller;

import com.renergetic.hdrapi.dao.DataDAO;
import com.renergetic.hdrapi.dao.DataWrapperDAO;
import com.renergetic.hdrapi.service.DataService;
import com.renergetic.hdrapi.service.InformationPanelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Data Controller", description = "Allows collect data about Influx measurement")
@RequestMapping("/api/data")
public class DataController {

    @Autowired
    DataService dataSv;
    @Autowired
    private InformationPanelService informationPanelService;

//=== GET REQUESTS ====================================================================================

    @Operation(summary = "Get Data related with a panel id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No dashboards found related with this user")
    })
    @GetMapping(path = "influxdb/panel/{panel_id}", produces = "application/json")
    public ResponseEntity<DataDAO> getDataByPanel(
            @PathVariable Long panel_id,
            @RequestParam("from") Optional<String> from,
            @RequestParam("to") Optional<String> to,
            @RequestParam("bucket") Optional<String> bucket,
            @RequestParam("field") Optional<String> field,
            @RequestParam Map<String, String> tags) {

        Map<String, String> params = new HashMap<>();
        if (from.isPresent()) params.put("from", from.get());
        if (to.isPresent()) params.put("to", from.get());
        if (bucket.isPresent()) params.put("bucket", from.get());
        if (field.isPresent()) params.put("field", from.get());
        if (tags != null) params.putAll(tags);

        return new ResponseEntity<>(dataSv.getByPanel(panel_id, params), HttpStatus.OK);
    }

    @Operation(summary = "Get Data related with a panel id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No dashboards found related with this user")
    })
    @GetMapping(path = "panel/{panelId}", produces = "application/json")
    public ResponseEntity<DataWrapperDAO> getPanelData(
            @PathVariable Long panelId,
            @RequestParam("from") Optional<Long> from,
            @RequestParam("to") Optional<Long> to) {
//			@RequestParam Map<String, String> tags){

//		Map<String, String> params = new HashMap<>();
        return new ResponseEntity<>(dataSv.getPanelData(panelId, from.orElse((new Date()).getTime() - 3600000), to),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Data related with a panel id and asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No dashboards found related with this user")
    })
    @GetMapping(path = "panel/{panelId}/asset/{assetId}", produces = "application/json")
    public ResponseEntity<DataWrapperDAO> getAssetPanelData(
            @PathVariable Long panelId,
            @PathVariable Long assetId,
//			@RequestParam("from") Optional<Long> from,
            @RequestParam("from") Optional<Long> from,
            @RequestParam("to") Optional<Long> to) {
//			@RequestParam Map<String, String> tags){


        DataWrapperDAO panelData =
                dataSv.getPanelData(panelId, assetId, from.orElse((new Date()).getTime() - 3600000), to);
        return new ResponseEntity<>(panelData, HttpStatus.OK);
    }

    @Operation(summary = "Get Data related with a tile id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No dashboards found related with this user")
    })
    @GetMapping(path = "tile/{tile_id}", produces = "application/json")
    public ResponseEntity<DataDAO> getDataByTile(
            @PathVariable Long tile_id,
            @RequestParam("from") Optional<String> from,
            @RequestParam("to") Optional<String> to,
            @RequestParam("bucket") Optional<String> bucket,
            @RequestParam("field") Optional<String> field,
            @RequestParam Map<String, String> tags) {

        Map<String, String> params = new HashMap<>();
        if (from.isPresent()) params.put("from", from.get());
        if (to.isPresent()) params.put("to", from.get());
        if (bucket.isPresent()) params.put("bucket", from.get());
        if (field.isPresent()) params.put("field", from.get());
        if (tags != null) params.putAll(tags);

        return new ResponseEntity<>(dataSv.getByPanel(tile_id, params), HttpStatus.OK);
    }

}
