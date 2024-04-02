package com.renergetic.ruleevaluationservice.service;

import com.google.gson.Gson;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.common.repository.*;
import com.renergetic.ruleevaluationservice.exception.ConfigurationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EVDRService {

    @Value("${ev-dr.executionCRON}")
    private String executionCRON;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetTypeRepository assetTypeRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private DemandDefinitionRepository demandDefinitionRepository;

    @Autowired
    private DemandScheduleRepository demandScheduleRepository;

    @Autowired
    private NotificationScheduleRepository notificationScheduleRepository;

    @Autowired
    private MeasurementAggregationService measurementAggregationService;

    @Autowired
    private RuleEvaluationService ruleEvaluationService;
    @Autowired
    private MeasurementAggregationRepository measurementAggregationRepository;

    private final Gson gson = new Gson();

    public void evaluateEVDR() throws ConfigurationError {
        //TODO: Replace this call later by the new asset type for VA GROUPING asset.
        List<Asset> assets = assetRepository.findDistinctByConnectionsConnectionTypeAndTypeName(ConnectionType.va_grouping, "pv_virtual_asset_group");

        LocalDateTime currentTime = LocalDateTime.now();

        CronExpression cronTrigger = CronExpression.parse(executionCRON);
        LocalDateTime nextExecution = cronTrigger.next(currentTime);

        for(Asset assetPVGroup : assets){
            if(!assetPVGroup.getDetails().stream().anyMatch(d -> d.getKey().equals("ev-dr-threshold")))
                continue;
            try{

                /*
                    TODO:
                    For now this is fine as there is only one for the PMB !
                    Later, should define a way to uniquely retrieve the aggregation for the EV-DR
                 */
                // Looking for a measurement that contains all pvIds in details.
                List<MeasurementAggregation> ma = measurementAggregationRepository.findByOutputMeasurementsAsset(assetPVGroup);

                if(ma.isEmpty() || ma.get(0).getOutputMeasurements().isEmpty())
                    return;

                Measurement finalMeasurement = ma.get(0).getOutputMeasurements().stream().findFirst().get();

                //TODO: Evaluate the aggregation first. Take decision here on timing to run it in the scheduler.
                measurementAggregationService.aggregateOne(finalMeasurement, ma.get(0));
                ruleEvaluationService.retrieveAndExecuteAllRulesForAssetId(assetPVGroup.getId());

                List<NotificationSchedule> ns = notificationScheduleRepository.findByAssetId(assetPVGroup.getId());
                if(ns.stream().anyMatch(s -> s.getDateFrom().isBefore(LocalDateTime.now()) &&
                        s.getDateTo().isAfter(LocalDateTime.now()) &&
                        s.getMeasurement().getId().equals(finalMeasurement.getId()))){
                    Asset asset = getAssetFromAssetDetailsToAssignRecommendation(assetPVGroup);
                    DemandDefinition dd = demandDefinitionRepository.findByActionTypeAndActionAndMessage(
                            DemandDefinitionActionType.START, DemandDefinitionAction.CHARGE_EV, "").orElseGet(() ->{
                        DemandDefinition demandDefinition = new DemandDefinition();
                        demandDefinition.setAction(DemandDefinitionAction.CHARGE_EV);
                        demandDefinition.setActionType(DemandDefinitionActionType.START);
                        demandDefinition.setMessage("");
                        return demandDefinition;
                    });
                    if(dd.getId() == null){
                        DemandSchedule demandSchedule = configureDemandSchedule(new DemandSchedule(), dd, currentTime, nextExecution, asset);
                        demandDefinitionRepository.save(dd);
                        demandScheduleRepository.save(demandSchedule);
                    } else {
                        DemandSchedule demandSchedule = demandScheduleRepository.findByAssetIdAndDemandDefinitionId(asset.getId(), dd.getId()).orElseGet(DemandSchedule::new);
                        configureDemandSchedule(demandSchedule, dd, currentTime, nextExecution, asset);
                        demandScheduleRepository.save(demandSchedule);
                    }
                }
            } catch(ConfigurationError ce){
                ce.printStackTrace();
            }
        }
    }

    private DemandSchedule configureDemandSchedule(DemandSchedule demandSchedule, DemandDefinition demandDefinition, LocalDateTime start, LocalDateTime stop, Asset asset){
        demandSchedule.setDemandDefinition(demandDefinition);
        demandSchedule.setDemandStart(start);
        demandSchedule.setDemandStop(stop);
        demandSchedule.setAsset(asset);
        demandSchedule.setUpdate(start);
        return demandSchedule;
    }

    private Asset getAssetFromAssetDetailsToAssignRecommendation(Asset asset) throws ConfigurationError {
        AssetDetails assetDetails = asset.getDetails().stream().filter(x -> x.getKey().equals("ev-dr-asset-recommendation")).findFirst().orElse(null);
        if(assetDetails == null)
            throw new ConfigurationError("Could not find an asset detail with 'ev-dr-asset-recommendation' key for asset: "+asset.getName());
        String name = assetDetails.getValue();

        Asset foundAsset = assetRepository.findByName(name).orElse(null);
        if(foundAsset == null)
            throw new ConfigurationError("Asset with name '"+name+"' could not be found for asset: "+asset.getName());
        return foundAsset;
    }
}
