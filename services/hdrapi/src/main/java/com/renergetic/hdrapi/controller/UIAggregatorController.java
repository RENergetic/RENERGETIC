package com.renergetic.hdrapi.controller;

import com.renergetic.hdrapi.dao.*;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.Domain;
import com.renergetic.hdrapi.service.*;
import com.renergetic.hdrapi.service.utils.DummyDataGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Ui aggregator Controller", description = "Aggregates data from multiple controller to simplify UI requests")
@RequestMapping("/api/ui")
public class UIAggregatorController {
    @Value("${api.generate.dummy-data}")
    private Boolean generateDummy;

    @Autowired
    private DemandRequestService demandRequestService;
    @Autowired
    private AssetService assetService;
    @Autowired
    private InformationPanelService informationPanelService;
    @Autowired
    private DataService dataService;
    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "API wrapper for front-end")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = {"/wrapper", "/wrapper/{userId}"}, produces = "application/json")
    public ResponseEntity<WrapperResponseDAO> apiWrapper(@PathVariable(required = false) String userId,
                                                         @RequestBody WrapperRequestDAO wrapperRequestBodyDAO) {
        //TODO: infer userId from auth headers
        WrapperResponseDAO wrapperResponseDAO = new WrapperResponseDAO();
        //
        if (wrapperRequestBodyDAO.getCalls().getAssets() != null && userId!=null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getAssets();
            wrapperResponseDAO.setAssets(getSimpleAssets(userId, Optional.ofNullable(data.getOffset()),
                    Optional.ofNullable(data.getLimit())));
        }
        if (wrapperRequestBodyDAO.getCalls().getAssetPanels() != null && userId!=null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getAssetPanels();
            wrapperResponseDAO.setAssetPanels(getAssetPanels(userId, Optional.ofNullable(data.getOffset()),
                    Optional.ofNullable(data.getLimit())));

        }
        if (wrapperRequestBodyDAO.getCalls().getData() != null) {
            //get data related to panelId -> TODO: something to consider later
        }
        if (wrapperRequestBodyDAO.getCalls().getDemands() != null && userId!=null) {
            //TODO: ask someone about public demands ?
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getDemands();
            wrapperResponseDAO.setDemands(getDemandSchedules(userId, Optional.ofNullable(data.getOffset()),
                    Optional.ofNullable(data.getLimit())));
            if (wrapperResponseDAO.getDemands().isEmpty() && generateDummy) {
                //generate some random demands
                List<DemandScheduleDAO> schedule =
                        demandRequestService.getByUserIdGroup(Long.parseLong(userId), 0, 10);
                schedule = DummyDataGenerator.getDemand(schedule);
                wrapperResponseDAO.setDemands(schedule);
            }
            List<MeasurementDAOResponse> measurements =
                    wrapperResponseDAO.getDemands().stream().filter(
                            it -> it.getDemandDefinition().getTile() != null
                    ).flatMap(demand -> demand.getDemandDefinition().getTile().getMeasurements().stream())
                            .collect(Collectors.toList());
            DataDAO demandData = dataService.getData(measurements, null, null);
            //TODO: here there might be issue with presenting the data and choosing appropriate time interval - this should be discused
            //probably data required for the demand demand should be stored as static  DataDAO JSON in RDBMS
            //if the user chooses interval it wouldnt make sense from the demand/request perspective
            wrapperResponseDAO.appendData(demandData);

        }
        if (wrapperRequestBodyDAO.getCalls().getPanels() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getPanels();
            wrapperResponseDAO.setPanels(
                    getPanels(userId, Optional.ofNullable(data.getOffset()), Optional.ofNullable(data.getLimit())));
        }
        if (wrapperRequestBodyDAO.getCalls().getDashboards() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getDashboards();
            wrapperResponseDAO.setDashboards(
                    getDashboards(userId, Optional.ofNullable(data.getOffset()), Optional.ofNullable(data.getLimit())));
        }

        return new ResponseEntity<>(wrapperResponseDAO, HttpStatus.OK);
    }

    @Operation(summary = "API wrapper to get all production for front-end")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/wrapper/production/{panelId}", produces = "application/json")
    @Deprecated
    public ResponseEntity<Map<String, Object>> apiWrapperProduction(
            @PathVariable Long panelId,
            @RequestParam(required = false) Optional<String> bucket,
            @RequestParam(required = false) Optional<Long> from,
            @RequestParam(required = false) Optional<Long> to) {

        Map<String, Object> ret = new HashMap<>();
        Instant fromInstant = Instant.ofEpochMilli(from.orElse(0L));
        Instant toInstant = Instant.ofEpochMilli(to.orElse(0L));

        // Set to a from parameters to search production of current year if they are empty
        if (from.isEmpty()) {
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DATE, 1);
            calendar.set(Calendar.MONTH, 0);
            fromInstant = calendar.toInstant();
        }
        if (to.isEmpty()) {
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.MILLISECOND, 999);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.DATE, 31);
            calendar.set(Calendar.MONTH, 11);
            toInstant = calendar.toInstant();
        }
        // Search panel with the ID sent
        ret.put("panel", informationPanelService.getById(panelId));

        // Get heat and electricity productions
        ret.put("heat", dataService.getProduction(bucket.orElse("renergetic"), fromInstant, toInstant, Domain.heat));
        ret.put("electricity",
                dataService.getProduction(bucket.orElse("renergetic"), fromInstant, toInstant, Domain.electricity));

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

//    private List<AssetDAOResponse> getAssets(String userId, Optional<Long> offset, Optional<Integer> limit) {
//        //not used function - can be deleted
//        try {
//            return assetService.findByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
//        } catch (NotFoundException ex) {
//            return new ArrayList<>();
//        }
//    }

    private List<SimpleAssetDAO> getSimpleAssets(String userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return assetService.findSimpleByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
        } catch (NotFoundException ex) {
            return new ArrayList<>();
        }
    }

    private List<DemandScheduleDAO> getDemandSchedules(String userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return demandRequestService.getByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
        } catch (NotFoundException ex) {
            return new ArrayList<>();
        }
    }

    private List<InformationPanelDAOResponse> getPanels(String userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return informationPanelService.findByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
        } catch (NotFoundException ex) {
            return new ArrayList<>();
        }
    }

    private List<AssetPanelDAO> getAssetPanels(String userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return assetService.findAssetsPanelsByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
        } catch (NotFoundException ex) {
            return new ArrayList<>();
        }
    }

    private DataDAO getData(String userId, Map<String, String> params) {
        return dataService.getByUserId(Long.parseLong(userId), params);
    }

    private List<DashboardDAO> getDashboards(String userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return dashboardService.getAvailableToUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
        } catch (NotFoundException ex) {
            return new ArrayList<>();
        }
    }

}
