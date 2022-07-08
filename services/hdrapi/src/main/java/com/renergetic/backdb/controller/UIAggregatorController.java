package com.renergetic.backdb.controller;

import com.renergetic.backdb.dao.*;
import com.renergetic.backdb.service.AssetService;
import com.renergetic.backdb.service.DemandRequestService;
import com.renergetic.backdb.service.InformationPanelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Ui aggregator Controller", description = "Aggregates data from multiple controller to simplify UI requests")
@RequestMapping("/api/ui")
public class UIAggregatorController {
    @Autowired
    private InformationTileController informationTileController;
    @Autowired
    private DemandRequestService demandRequestService;
    @Autowired
    private AssetService assetService;
    @Autowired
    private InformationPanelService informationPanelService;

    @Operation(summary = "API wrapper for front-end")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/wrapper/{userId}", produces = "application/json")
    public ResponseEntity<WrapperResponseDAO> apiWrapper(@PathVariable String userId, @RequestBody WrapperRequestDAO wrapperRequestBodyDAO){
        WrapperResponseDAO wrapperResponseDAO = new WrapperResponseDAO();

        if(wrapperRequestBodyDAO.getCalls().getAssets() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getAssets();
            wrapperResponseDAO.setAssets(getAssets(userId, Optional.ofNullable(data.getOffset()), Optional.ofNullable(data.getLimit())));
        }
        if(wrapperRequestBodyDAO.getCalls().getAssetPanels() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getAssetPanels();
            wrapperResponseDAO.setAssetPanels(getAssetPanels(userId, Optional.ofNullable(data.getOffset()), Optional.ofNullable(data.getLimit())));
        }
        if(wrapperRequestBodyDAO.getCalls().getData() != null) {
            //TODO
        }
        if(wrapperRequestBodyDAO.getCalls().getDemands() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getDemands();
            wrapperResponseDAO.setDemands(getDemandSchedules(userId, Optional.ofNullable(data.getOffset()), Optional.ofNullable(data.getLimit())));
        }
        if(wrapperRequestBodyDAO.getCalls().getPanels() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getPanels();
            wrapperResponseDAO.setPanels(getPanels(userId, Optional.ofNullable(data.getOffset()), Optional.ofNullable(data.getLimit())));
        }

        return new ResponseEntity<>(wrapperResponseDAO, HttpStatus.OK);
    }

    private List<AssetDAOResponse> getAssets(String userId, Optional<Long> offset, Optional<Integer> limit){
        // Ok and ready
        return assetService.findByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
    }

    private List<DemandScheduleDAO> getDemandSchedules(String userId, Optional<Long> offset, Optional<Integer> limit){
        // Ok and ready
        return demandRequestService.getByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
    }

    private List<InformationPanelDAOResponse> getPanels(String userId, Optional<Long> offset, Optional<Integer> limit){
        return informationPanelService.findByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
    }

    private List<AssetPanelDAO> getAssetPanels(String userId, Optional<Long> offset, Optional<Integer> limit){
        return assetService.findAssetsPanelsByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
    }

    private List<Object> getData(String userId, Optional<Long> offset, Optional<Integer> limit){
        return null;
    }

}
