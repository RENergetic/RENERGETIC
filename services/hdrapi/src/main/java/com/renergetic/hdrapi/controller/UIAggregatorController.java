package com.renergetic.hdrapi.controller;

import com.renergetic.common.dao.*;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.exception.UnauthorizedAccessException;
import com.renergetic.common.model.ConnectionType;
import com.renergetic.common.model.User;
import com.renergetic.common.model.security.KeycloakRole;
import com.renergetic.common.repository.MeasurementTypeRepository;
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
            wrapperResponseDAO.setAssets(getOwnerAssets(userId, Optional.ofNullable(data.getOffset()),
                    Optional.ofNullable(data.getLimit())));
        }
        if (body.getCalls().getAssetPanels() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = body.getCalls().getAssetPanels();
            wrapperResponseDAO.setAssetPanels(getPrivateAssetPanels(userId, Optional.ofNullable(data.getOffset()),
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
            wrapperResponseDAO.setPanels(                    getPublicPanels( Optional.ofNullable(data.getLimit())));
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

    private List<SimpleAssetDAO> getOwnerAssets(Long userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return assetService.findSimpleByUserId(userId, List.of(ConnectionType.owner), offset.orElse(0L), limit.orElse(20));
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

    private List<InformationPanelDAOResponse> getPublicPanels(Optional<Integer> limit) {
        try {
            return informationPanelService.findFeatured(false,Math.min(50, limit.orElse(20)));
        } catch (NotFoundException ex) {
            return new ArrayList<>();
        }
    }

    /**
     * private dashboards
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    private List<AssetPanelDAO> getPrivateAssetPanels(Long userId, Optional<Long> offset, Optional<Integer> limit) {
        try {
            return assetService.findAssetsPanelsByUserId(userId, List.of(ConnectionType.owner, ConnectionType.resident), offset.orElse(0L), limit.orElse(20));
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