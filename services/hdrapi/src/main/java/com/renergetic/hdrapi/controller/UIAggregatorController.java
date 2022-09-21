package com.renergetic.hdrapi.controller;

import com.renergetic.hdrapi.dao.*;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @PostMapping(path = "/wrapper/{userId}", produces = "application/json")
    public ResponseEntity<WrapperResponseDAO> apiWrapper(@PathVariable String userId,
                                                         @RequestBody WrapperRequestDAO wrapperRequestBodyDAO) {
        WrapperResponseDAO wrapperResponseDAO = new WrapperResponseDAO();

        if (wrapperRequestBodyDAO.getCalls().getAssets() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getAssets();
            wrapperResponseDAO.setAssets(
                    getSimpleAssets(userId, Optional.ofNullable(data.getOffset()),
                            Optional.ofNullable(data.getLimit())));
        }
        if (wrapperRequestBodyDAO.getCalls().getAssetPanels() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getAssetPanels();
            wrapperResponseDAO.setAssetPanels(getAssetPanels(userId, Optional.ofNullable(data.getOffset()),
                    Optional.ofNullable(data.getLimit())));
        }
        if (wrapperRequestBodyDAO.getCalls().getData() != null) {
            WrapperRequestDAO.InfluxArgsWrapperRequestDAO args = wrapperRequestBodyDAO.getCalls().getData();
            Map<String, String> params = new HashMap<>();

            if (args.getFrom() != null) params.put("from", args.parseFrom());
            if (args.getTo() != null) params.put("to", args.parseTo());
            if (args.getBucket() != null) params.put("bucket", args.getBucket());
//            if (args.getField() != null) params.put("field", args.getField());
            if (args.getTags() != null) params.putAll(args.getTags());

            wrapperResponseDAO.appendData(getData(userId, params));
        }
        if (wrapperRequestBodyDAO.getCalls().getDemands() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = wrapperRequestBodyDAO.getCalls().getDemands();
            wrapperResponseDAO.setDemands(getDemandSchedules(userId, Optional.ofNullable(data.getOffset()),
                    Optional.ofNullable(data.getLimit())));
            if (wrapperResponseDAO.getDemands().isEmpty() && generateDummy) {
                //tODO: if test mode
                List<DemandScheduleDAO> schedule =
                        demandRequestService.getByUserIdGroup(Long.parseLong(userId), 0, 10);
                schedule = DummyDataGenerator.getDemand(schedule);
                wrapperResponseDAO.setDemands(schedule);
            }
            if(generateDummy) {
            	DataDAO demandData = DummyDataGenerator.getDemandData(wrapperResponseDAO.getDemands());
            	wrapperResponseDAO.appendData(demandData);
            }
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
    	ret.put("electricity", dataService.getProduction(bucket.orElse("renergetic"), fromInstant, toInstant, Domain.electricity));
    	
    	return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    private List<AssetDAOResponse> getAssets(String userId, Optional<Long> offset, Optional<Integer> limit) {
        return assetService.findByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
    }

    private List<SimpleAssetDAO> getSimpleAssets(String userId, Optional<Long> offset, Optional<Integer> limit) {
        return assetService.findSimpleByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
    }

    private List<DemandScheduleDAO> getDemandSchedules(String userId, Optional<Long> offset, Optional<Integer> limit) {
        return demandRequestService.getByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
    }

    private List<InformationPanelDAOResponse> getPanels(String userId, Optional<Long> offset, Optional<Integer> limit) {
        return informationPanelService.findByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
    }

    private List<AssetPanelDAO> getAssetPanels(String userId, Optional<Long> offset, Optional<Integer> limit) {
        return assetService.findAssetsPanelsByUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
    }

    private DataDAO getData(String userId, Map<String, String> params) {
        return dataService.getByUserId(Long.parseLong(userId), params);
    }

    private List<DashboardDAO> getDashboards(String userId, Optional<Long> offset, Optional<Integer> limit) {
        return dashboardService.getAvailableToUserId(Long.parseLong(userId), offset.orElse(0L), limit.orElse(20));
    }

}
