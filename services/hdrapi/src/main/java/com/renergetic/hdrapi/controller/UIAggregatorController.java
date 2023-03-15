package com.renergetic.hdrapi.controller;

import com.renergetic.hdrapi.dao.*;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.exception.UnauthorizedAccessException;
import com.renergetic.hdrapi.model.User;
import com.renergetic.hdrapi.model.security.KeycloakRole;
import com.renergetic.hdrapi.repository.MeasurementTypeRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private DummyDataGenerator dummyDataGenerator;
    @Autowired
    private MeasurementTypeRepository measurementTypeRepository;
    @Autowired
    private AssetService assetService;
    @Autowired
    private InformationPanelService informationPanelService;
    @Autowired
    private DataService dataService;
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    MeasurementService measurementSv;
    @Autowired
    LoggedInService loggedInService;

    @Operation(summary = "API wrapper for front-end")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/wrapper", produces = "application/json")
    public ResponseEntity<WrapperResponseDAO> apiWrapper(@RequestBody WrapperRequestDAO wrapperRequestBodyDAO) {
        User user = loggedInService.getLoggedInUser();
        if (user == null) {
        	throw new UnauthorizedAccessException("Invalid user, an authenticated user is required");
        }
        Long userId = user.getId();

        WrapperResponseDAO wrapperResponseDAO = createWrapperResponse(userId, wrapperRequestBodyDAO);
        return new ResponseEntity<>(wrapperResponseDAO, HttpStatus.OK);
    }

    @Operation(summary = "API wrapper for front-end")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/wrapper/{userId}", produces = "application/json")
    public ResponseEntity<WrapperResponseDAO> apiWrapperAdmin(@PathVariable(required = true) Long userId,
                                                         @RequestBody WrapperRequestDAO wrapperRequestBodyDAO) {
        User user = loggedInService.getLoggedInUser();
        if (user == null) {
        	throw new UnauthorizedAccessException("Invalid user, an authenticated user is required");
        } else if (!loggedInService.hasRole(KeycloakRole.REN_ADMIN.mask))
        	throw new UnauthorizedAccessException("Unauthorized user, you should have admin privileges");

        WrapperResponseDAO wrapperResponseDAO = createWrapperResponse(userId, wrapperRequestBodyDAO);
        return new ResponseEntity<>(wrapperResponseDAO, HttpStatus.OK);
    }

    private WrapperResponseDAO createWrapperResponse(Long userId, WrapperRequestDAO body) {

        WrapperResponseDAO wrapperResponseDAO = new WrapperResponseDAO();

        if (body.getCalls().getAssets() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = body.getCalls().getAssets();
            wrapperResponseDAO.setAssets(getSimpleAssets(userId, Optional.ofNullable(data.getOffset()),
                    Optional.ofNullable(data.getLimit())));
        }
        if (body.getCalls().getAssetPanels() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = body.getCalls().getAssetPanels();
            wrapperResponseDAO.setAssetPanels(getAssetPanels(userId, Optional.ofNullable(data.getOffset()),
                    Optional.ofNullable(data.getLimit())));
        }
        if (body.getCalls().getMeasurementTypes() != null) {
            wrapperResponseDAO.setMeasurementTypes(measurementSv.getTypes(null, 0, 1000));
        }
        if (body.getCalls().getData() != null) {
            //get data related to panelId -> TODO: something to consider later
        }
        if (body.getCalls().getAssetMetaKeys() != null) {
            wrapperResponseDAO.setAssetMetaKeys(
                    new AssetMetaKeys(assetService.listTypes(), assetService.listCategories()));
            //get data related to panelId -> TODO: something to consider later
        }

        if (body.getCalls().getPanels() != null) {
            //also called public dashboards
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = body.getCalls().getPanels();
            wrapperResponseDAO.setPanels(
                    getPanels(userId, Optional.ofNullable(data.getOffset()), Optional.ofNullable(data.getLimit())));
        }
        if (body.getCalls().getDashboards() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = body.getCalls().getDashboards();
            List<DashboardDAO> dashboards =
                    getDashboards(userId, Optional.ofNullable(data.getOffset()), Optional.ofNullable(data.getLimit()));
            if (dashboards.isEmpty() && generateDummy) {
                //generate some random demands
                dashboards = dummyDataGenerator.getDashboards(5);

            }
            DashboardMetaKeys meta = new DashboardMetaKeys(measurementTypeRepository.findByDashboardVisibility());
            wrapperResponseDAO.setDashboardMetaKeys(meta);
            wrapperResponseDAO.setDashboards(dashboards);


        }
        if (body.getCalls().getDemands() != null) {
            //TODO: ask someone about public demands ?
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = body.getCalls().getDemands();
            wrapperResponseDAO.setDemands(getDemandSchedules(userId, Optional.ofNullable(data.getOffset()),
                    Optional.ofNullable(data.getLimit())));
            if (wrapperResponseDAO.getDemands().isEmpty() && generateDummy) {
                //generate some random demands
                List<DemandScheduleDAO> schedule =
                        demandRequestService.getByUserIdGroup(userId, 0, 10);

                schedule = DummyDataGenerator.getDemand(schedule);
                wrapperResponseDAO.setDemands(schedule);
            }
            List<MeasurementDAOResponse> measurements =
                    wrapperResponseDAO.getDemands().stream().filter(
                                    it -> it.getDemandDefinition().getTile() != null
                            ).flatMap(demand -> demand.getDemandDefinition().getTile().getMeasurements().stream())
                            .collect(Collectors.toList());
            DataDAO demandData = dataService.getData(
                    measurements.stream().map(MeasurementDAOResponse::mapToEntity).collect(Collectors.toList()), null,
                    Optional.empty());
            //TODO: here there might be issue with presenting the data and choosing appropriate time interval - this should be discused
            //probably data required for the demand demand should be stored as static  DataDAO JSON in RDBMS
            //if the user chooses interval it wouldnt make sense from the demand/request perspective
            measurements.forEach(System.err::println);
            wrapperResponseDAO.appendData(demandData);
        }
        return wrapperResponseDAO;
    }

    private List<SimpleAssetDAO> getSimpleAssets(Long userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return assetService.findSimpleByUserId(userId, offset.orElse(0L), limit.orElse(20));
        } catch (NotFoundException ex) {
            return new ArrayList<>();
        }
    }

    private List<DemandScheduleDAO> getDemandSchedules(Long userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return demandRequestService.getByUserId(userId, offset.orElse(0L), limit.orElse(20));
        } catch (NotFoundException ex) {
            return new ArrayList<>();
        }
    }

    private List<InformationPanelDAOResponse> getPanels(Long userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return informationPanelService.findByUserId(userId, offset.orElse(0L), limit.orElse(20));
        } catch (NotFoundException ex) {
            return new ArrayList<>();
        }
    }

    private List<AssetPanelDAO> getAssetPanels(Long userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return assetService.findAssetsPanelsByUserId(userId, offset.orElse(0L), limit.orElse(20));
        } catch (NotFoundException ex) {
            return new ArrayList<>();
        }
    }

    private List<DashboardDAO> getDashboards(Long userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return dashboardService.getAvailableToUserId(userId, offset.orElse(0L), limit.orElse(20));
        } catch (NotFoundException ex) {
            return new ArrayList<>();
        }
    }

}