package com.renergetic.ruleevaluationservice.service;

import com.renergetic.common.model.*;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.common.repository.*;
import com.renergetic.ruleevaluationservice.dao.DataResponse;
import com.renergetic.ruleevaluationservice.exception.ConfigurationError;
import com.renergetic.ruleevaluationservice.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class EVDRService {
    @Value("${ev-dr.asset-type-name}")
    private String assetTypePvPanelGroup;

    @Value("${ev-dr.executionCRON}")
    private String executionCRON;

    @Autowired
    private DataService dataService;

    @Autowired
    private AssetTypeRepository assetTypeRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private DemandDefinitionRepository demandDefinitionRepository;

    @Autowired
    private DemandScheduleRepository demandScheduleRepository;

    public void evaluateEVDR() throws ConfigurationError {
        AssetType assetType = getAssetType();
        List<Asset> assets = getAssetMatchingType(assetType);

        LocalDateTime currentTime = LocalDateTime.now();

        CronExpression cronTrigger = CronExpression.parse(executionCRON);
        LocalDateTime nextExecution = cronTrigger.next(currentTime);

        for(Asset assetPVGroup : assets){
            try{
                double threshold = getThresholdFromAssetDetails(assetPVGroup);
                List<Asset> pvs = getAssetsLinkingParentAsset(assetPVGroup);

                double totalGeneration = getTotalGenerationValueFromAssets(pvs);

                if(totalGeneration >= threshold){
                    Asset asset = getAssetFromAssetDetailsToAssignRecommendation(assetPVGroup);
                    //TODO: Get Demand definition by actionType, action and message
                    //TODO: Get the existing demand schedule (linked to demand definition) (or create)
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
                        DemandSchedule demandSchedule = demandScheduleRepository.findByAssetIdAndDemandId(asset.getId(), dd.getId()).orElseGet(DemandSchedule::new);
                        demandSchedule = configureDemandSchedule(demandSchedule, dd, currentTime, nextExecution, asset);
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

    private AssetType getAssetType() throws ConfigurationError {
        AssetType assetType = assetTypeRepository.findByName(assetTypePvPanelGroup).orElse(null);
        if(assetType == null)
            throw new ConfigurationError("Could not find an asset type with name: "+assetTypePvPanelGroup);
        return assetType;
    }

    private List<Asset> getAssetMatchingType(AssetType assetType){
        return assetRepository.findByType(assetType);
    }

    private double getThresholdFromAssetDetails(Asset asset) throws ConfigurationError {
        AssetDetails assetDetails = asset.getDetails().stream().filter(x -> x.getKey().equals("ev-dr-threshold")).findFirst().orElse(null);
        if(assetDetails == null)
            throw new ConfigurationError("Could not find an asset detail with 'ev-dr-threshold' key for asset: "+asset.getName());
        try{
            return Double.parseDouble(assetDetails.getValue());
        } catch (NumberFormatException nfe){
            throw new ConfigurationError("Asset detail with 'ev-dr-threshold' key is an invalid number for asset: "+asset.getName());
        }
    }

    private List<Asset> getAssetsLinkingParentAsset(Asset asset) {
        return assetRepository.findByParentAsset(asset);
    }

    private Double getTotalGenerationValueFromAssets(List<Asset> assets){
        List<Double> values = new ArrayList<>();
        Set<Thread> threads = new HashSet<>();
        for(Asset asset : assets){
            //TODO: Check that the correct value is fetch !
            Measurement measurement = measurementRepository.findByAsset(asset).stream()
                    .filter(x -> (x.getDirection() != null && x.getDirection().equals(Direction.out) &&
                            x.getDomain() != null && x.getDomain().equals(Domain.electricity) &&
                            x.getSensorName() != null && !x.getSensorName().equals("pv")))
                    .findFirst().orElse(null);

            if(measurement != null){
                Thread thread = new Thread(() -> {
                    DataResponse dataResponse = dataService.getData(measurement, "last", "3h",
                            Instant.now().minus(3, ChronoUnit.HOURS).toEpochMilli(), Optional.empty());

                    values.add(Double.parseDouble(dataResponse.getDataUsedForComparison().getValue()));
                });
                thread.start();
                threads.add(thread);
            }
        }
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return values.stream().reduce(0.0, Double::sum);
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
