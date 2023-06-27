package com.renergetic.hdrapi.controller;

import com.renergetic.hdrapi.dao.DataWrapperDAO;
import com.renergetic.hdrapi.dao.TimeseriesDAO;
import com.renergetic.hdrapi.service.DataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Data Controller", description = "Allows collect data about Influx measurement")
@RequestMapping("/api/data")
public class DataController {
    //TODO: predictions

    @Autowired
    DataService dataSv;

    //=== GET REQUESTS ====================================================================================
    @Operation(summary = "Get Data related with a panel id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No dashboards found related with this user")
    })
    @GetMapping(path = "/panel/{panelId}", produces = "application/json")
    public ResponseEntity<DataWrapperDAO> getPanelData(
            @PathVariable Long panelId,
            @RequestParam("from") Optional<Long> from,
            @RequestParam("to") Optional<Long> to,
            @RequestParam("timeAggregation") Optional<String> timeAggregation,
            @RequestParam("timeAggregationFunction") Optional<String> timeAggregationFunction) {
    	// GET INSTANT TO FIRST DAY OF THE CURRENT 

		Long fromInstant = null;
    	
        if (from.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DATE, 1);
            calendar.set(Calendar.MONTH, 0);
            fromInstant = calendar.toInstant().toEpochMilli();
        }
        return new ResponseEntity<>(dataSv.getPanelData(panelId, from.orElse(fromInstant), to, timeAggregation, timeAggregationFunction),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Data related with a panel id and asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No dashboards found related with this user")
    })
    @GetMapping(path = "/panel/{panelId}/asset/{assetId}", produces = "application/json")
    public ResponseEntity<DataWrapperDAO> getAssetPanelData(
            @PathVariable Long panelId,
            @PathVariable Long assetId,
            @RequestParam("from") Optional<Long> from,
            @RequestParam("to") Optional<Long> to,
            @RequestParam("timeAggregation") Optional<String> timeAggregation,
            @RequestParam("timeAggregationFunction") Optional<String> timeAggregationFunction) {
        DataWrapperDAO panelData =
                dataSv.getPanelData(panelId, assetId, from.orElse((new Date()).getTime() - 3600000), to, timeAggregation, timeAggregationFunction);
        return new ResponseEntity<>(panelData, HttpStatus.OK);
    }
    @GetMapping(path = "/timeseries/tile/{tileId}", produces = "application/json")
    public ResponseEntity<TimeseriesDAO> getTileTimeseries(
            @PathVariable Long tileId,
            @RequestParam("from") Optional<Long> from,
            @RequestParam("to") Optional<Long> to) {
        TimeseriesDAO tileTimeseries =
                dataSv.getTileTimeseries(  tileId, null, from.orElse((new Date()).getTime() - 3600000), to);
        return new ResponseEntity<>(tileTimeseries, HttpStatus.OK);
    }



    @GetMapping(path = "/timeseries/tile/{tileId}/asset/{assetId}", produces = "application/json")
    public ResponseEntity<TimeseriesDAO> getTileAssetTimeseries(
            @PathVariable Long tileId,
            @PathVariable Long assetId,
            @RequestParam("from") Optional<Long> from,
            @RequestParam("to") Optional<Long> to) {
        TimeseriesDAO tileTimeseries =
                dataSv.getTileTimeseries( tileId, assetId, from.orElse((new Date()).getTime() - 3600000), to);
        return new ResponseEntity<>(tileTimeseries, HttpStatus.OK);
    }
    //TODO:
//    @GetMapping(path = "/timeseries/panel/{panelId}", produces = "application/json")
//    public ResponseEntity<TimeseriesDAO> getTileTimeseries(
//            @PathVariable Long panelId,
//            @RequestParam("from") Optional<Long> from,
//            @RequestParam("to") Optional<Long> to) {
//        TimeseriesDAO tileTimeseries =
//                dataSv.getTileTimeseries(  tileId, null, from.orElse((new Date()).getTime() - 3600000), to);
//        return new ResponseEntity<>(tileTimeseries, HttpStatus.OK);
//    } @GetMapping(path = "/timeseries/panel/{panelId}/asset/{assetId}", produces = "application/json")
//    public ResponseEntity<TimeseriesDAO> getTileAssetTimeseries(
//            @PathVariable Long panelId,
//            @PathVariable Long assetId,
//            @RequestParam("from") Optional<Long> from,
//            @RequestParam("to") Optional<Long> to) {
//        TimeseriesDAO tileTimeseries =
//                dataSv.getTileTimeseries(panelId,tileId, assetId, from.orElse((new Date()).getTime() - 3600000), to);
//        return new ResponseEntity<>(tileTimeseries, HttpStatus.OK);
//    }
}
