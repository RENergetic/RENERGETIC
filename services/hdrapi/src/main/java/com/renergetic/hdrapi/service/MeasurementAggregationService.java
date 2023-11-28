package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.OptimizerTypeDAO;
import com.renergetic.common.dao.aggregation.AggregationConfigurationDAO;
import com.renergetic.common.dao.aggregation.MeasurementAggregationDAO;
import com.renergetic.common.dao.aggregation.ParamaterAggregationConfigurationDAO;
import com.renergetic.common.exception.InvalidArgumentException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.MeasurementAggregationRepository;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.common.repository.OptimizerTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MeasurementAggregationService {
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private MeasurementAggregationRepository measurementAggregationRepository;
    @Autowired
    private OptimizerTypeRepository optimizerTypeRepository;
    @Autowired
    private MeasurementRepository measurementRepository;

    public AggregationConfigurationDAO get(Long assetId) {
        Asset asset = assetRepository.findById(assetId).orElseThrow(NotFoundException::new);

        List<MeasurementAggregation> measurementAggregations = measurementAggregationRepository.findByOutputMeasurementsAsset(asset);

        OptimizerType optimizerType = optimizerTypeRepository.findByName(asset.getDetails().stream()
                .filter(x -> x.getKey().equals("optimizer_type")).findFirst().orElse(new AssetDetails()).getValue());

        return AggregationConfigurationDAO.create(asset, measurementAggregations, optimizerType);
    }

    @Transactional
    public AggregationConfigurationDAO save(Long assetId, AggregationConfigurationDAO aggregationConfigurationDAO) {
        Asset asset = assetRepository.findById(assetId).orElseThrow(NotFoundException::new);

        setAssetDetail(asset, "optimizer_type", aggregationConfigurationDAO.getMvoComponentType().getType());
        setAssetDetail(asset, "domain_a", aggregationConfigurationDAO.getMvoComponentType().getDomainA());
        setAssetDetail(asset, "domain_b", aggregationConfigurationDAO.getMvoComponentType().getDomainB());

        List<MeasurementAggregation> measurementAggregations = new ArrayList<>();
        for(MeasurementAggregationDAO measurementAggregationDAO : aggregationConfigurationDAO.getMeasurementAggregations()){
            //TODO: Check if we need to store the optimizer type and domains in measurement ??
            MeasurementAggregation measurementAggregation = new MeasurementAggregation();

            List<Measurement> aggregatedMeasurements = measurementRepository.findByIds(measurementAggregationDAO.getMeasurements());
            checkMeasurementsValidity(aggregatedMeasurements, measurementAggregationDAO.getMeasurements());
            measurementAggregation.setAggregatedMeasurements(aggregatedMeasurements);
            Measurement ref = aggregatedMeasurements.get(0);

            measurementAggregation.setOutputMeasurements(measurementAggregationDAO.getOutputs().stream().map(x -> {
                Measurement measurement = new Measurement();
                measurement.setType(ref.getType());
                measurement.setDomain(ref.getDomain());
                measurement.setDirection(ref.getDirection());
                measurement.setName(ref.getName());
                measurement.setSensorName(ref.getSensorName());
                measurement.setSensorId(ref.getSensorId());
                measurement.setIsland(ref.getIsland());
                measurement.setAsset(asset);
                measurement.setDetails(ref.getDetails().stream().map(d -> new MeasurementDetails(d.getKey(), d.getValue(), null)).collect(Collectors.toList()));
                measurement.getDetails().add(new MeasurementDetails("aggregation_type", x.getAggregationType(), null));
                measurement.getDetails().add(new MeasurementDetails("time_min", x.getTimeMin(), null));
                measurement.getDetails().add(new MeasurementDetails("time_max", x.getTimeMax(), null));
                measurement.getDetails().add(new MeasurementDetails("time_range", x.getTimeRange(), null));
                return measurement;
            }).collect(Collectors.toList()));

            measurementAggregation.setDomainA(Domain.valueOf(aggregationConfigurationDAO.getMvoComponentType().getDomainA()));
            measurementAggregation.setDomainB(Domain.valueOf(aggregationConfigurationDAO.getMvoComponentType().getDomainB()));

            measurementAggregations.add(measurementAggregation);
        }

        asset.getDetails().removeIf(x -> x.getKey().contains("_aggregation"));
        asset.getDetails().addAll(aggregationConfigurationDAO.getParameterAggregationConfigurations().entrySet().stream()
                .map(e -> new AssetDetails(e.getKey(), e.getValue().getAggregation()+"_aggregation", asset))
                .collect(Collectors.toList()));

        assetRepository.save(asset);
        measurementAggregationRepository.deleteAll(measurementAggregationRepository.findByOutputMeasurementsAsset(asset));
        measurementAggregationRepository.saveAll(measurementAggregations);

        return get(assetId);

        //TODO:

        /*
            TODO:
            A:
            1. Fetch all measurements aggregation related to this asset
            2. For each MeasurementAggregationDAO:
                - Create a measurement aggregation with specific data
                    > Validate that measurements are compatible
                - Create a measurement with specific details
                    > All details (don't forget the MVO maybe ?)
            3. In transaction:
                > Delete all measurements aggregation / measurements related to asset
                > Save all entities

            B:
            1. Delete all *_aggregation on the asset
            2. Save all params as *_aggregation on the asset
         */
    }

    public List<OptimizerTypeDAO> getOptimizerTypeList() {
        return optimizerTypeRepository.findAll().stream().map(OptimizerTypeDAO::create).collect(Collectors.toList());
    }

    public Map<String, ParamaterAggregationConfigurationDAO> getParametersForOptimizerType(Long assetId, String type) {
        Asset asset = assetRepository.findById(assetId).orElseThrow(NotFoundException::new);
        OptimizerType optimizerType = optimizerTypeRepository.findByName(type);

        return ParamaterAggregationConfigurationDAO.create(asset, optimizerType);
    }

    private void checkMeasurementsValidity(List<Measurement> measurements, List<Long> ids){
        Measurement ref = null;
        Map<String, String> details = new HashMap<>();
        List<String> remainingKeys = new ArrayList<>();
        for(Measurement measurement : measurements){
            if(ref == null){
                ref = measurement;
                ref.getDetails().forEach(x -> details.put(x.getKey(), x.getValue()));
                remainingKeys.addAll(details.keySet());
                continue;
            }

            //This could be simplified, but the idea is to keep the exact field error for the user.
            if(!Objects.equals(measurement.getDirection(), ref.getDirection()))
                throw new InvalidArgumentException(
                        String.format("incompatible measurements: direction is incompatible between measurements %s ans %s",
                                measurement.getId(), ref.getId()));
            if(!Objects.equals(measurement.getDomain(), ref.getDomain()))
                throw new InvalidArgumentException(
                        String.format("incompatible measurements: domain is incompatible between measurements %s ans %s",
                                measurement.getId(), ref.getId()));
            if(!Objects.equals(measurement.getName(), ref.getName()))
                throw new InvalidArgumentException(
                        String.format("incompatible measurements: name is incompatible between measurements %s ans %s",
                                measurement.getId(), ref.getId()));
            if(!Objects.equals(measurement.getSensorName(), ref.getSensorName()))
                throw new InvalidArgumentException(
                        String.format("incompatible measurements: sensor name is incompatible between measurements %s ans %s",
                                measurement.getId(), ref.getId()));
            if(!Objects.equals(measurement.getSensorId(), ref.getSensorId()))
                throw new InvalidArgumentException(
                        String.format("incompatible measurements: sensor id is incompatible between measurements %s ans %s",
                                measurement.getId(), ref.getId()));

            Measurement finalRef = ref;
            measurement.getDetails().forEach(x -> {
                if(details.containsKey(x.getKey()) || !remainingKeys.remove(x.getKey())) {
                    String value = details.get(x.getKey());
                    if(!Objects.equals(value, x.getKey()))
                        throw new InvalidArgumentException(
                                String.format("incompatible measurements: measurement detail %s is different between measurements %s ans %s",
                                        x.getKey(), measurement.getId(), finalRef.getId()));
                }
                else
                    throw new InvalidArgumentException(
                            String.format("incompatible measurements: measurement detail %s is missing between measurements %s ans %s",
                                    x.getKey(), measurement.getId(), finalRef.getId()));
            });

            if(!remainingKeys.isEmpty()){
                StringBuilder b = new StringBuilder();
                remainingKeys.forEach(b::append);
                throw new InvalidArgumentException(
                        String.format("incompatible measurements: the following measurements details are missing between measurements %s ans %s: %s",
                                measurement.getId(), finalRef.getId(), b));
            }

            remainingKeys.addAll(details.keySet());
        }
    }

    private void setAssetDetail(Asset asset, String key, String newValue){
        AssetDetails detail = asset.getDetails().stream()
                .filter(x -> x.getKey().equals(key)).findFirst().orElse(null);

        if(detail == null)
            asset.getDetails().add(new AssetDetails(key, newValue,
                    asset));
        else
            detail.setValue(newValue);
    }
}
