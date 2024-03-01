package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.OptimizerTypeDAO;
import com.renergetic.common.dao.aggregation.AggregationConfigurationDAO;
import com.renergetic.common.dao.aggregation.MeasurementAggregationDAO;
import com.renergetic.common.exception.InvalidArgumentException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.*;
import com.renergetic.common.model.UUID;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.*;
import com.renergetic.common.repository.information.AssetDetailsRepository;
import com.renergetic.common.repository.information.MeasurementDetailsRepository;
import com.renergetic.hdrapi.dao.MeasurementAggregationMeasurementSelectionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MeasurementAggregationService {
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetConnectionRepository assetConnectionRepository;
    @Autowired
    private MeasurementAggregationRepository measurementAggregationRepository;
    @Autowired
    private OptimizerTypeRepository optimizerTypeRepository;
    @Autowired
    private OptimizerParameterRepository optimizerParameterRepository;
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private UuidRepository uuidRepository;

    @Autowired
    private AssetDetailsRepository assetDetailsRepository;
    @Autowired
    private MeasurementDetailsRepository measurementDetailsRepository;
    @Autowired
    private MeasurementTagsRepository measurementTagsRepository;

    public AggregationConfigurationDAO get(Long assetId) {
        Asset asset = assetRepository.findById(assetId).orElseThrow(NotFoundException::new);

        List<MeasurementAggregation> measurementAggregations = measurementAggregationRepository.findByOutputMeasurementsAsset(asset);

        OptimizerType optimizerType = optimizerTypeRepository.findByName(asset.getDetails().stream()
                .filter(x -> Objects.equals(x.getKey(),"optimizer_type")).findFirst().orElse(new AssetDetails()).getValue());

        return AggregationConfigurationDAO.create(asset, measurementAggregations, optimizerType, optimizerParameterRepository.findAll());
    }

    @Transactional
    public AggregationConfigurationDAO save(Long assetId, AggregationConfigurationDAO aggregationConfigurationDAO) {
        Asset asset = assetRepository.findById(assetId).orElseThrow(NotFoundException::new);

        Long domainA = aggregationConfigurationDAO.getMvoComponentType().getDomainA();
        Long domainB = aggregationConfigurationDAO.getMvoComponentType().getDomainB();
        OptimizerType optimizerType = optimizerTypeRepository.findByName(aggregationConfigurationDAO.getMvoComponentType().getType());
        AssetDetails existingOptimizer = asset.getDetails().stream().filter(x -> x.getKey().equals("optimizer_type")).findFirst().orElse(null);

        if(existingOptimizer != null && existingOptimizer.getValue() != null && !existingOptimizer.getValue().isEmpty()){
            AssetConnection assetConnection = new AssetConnection();
            assetConnection.setConnectedAsset(asset);

            OptimizerType existingOptimizerType = optimizerTypeRepository.findByName(existingOptimizer.getValue());

            List<AssetConnection> expiredConnections = assetConnectionRepository.findAll(Example.of(assetConnection)).stream().filter(con -> {
                ConnectionType connectionType = con.getConnectionType();
                return connectionType != null &&
                        (connectionType.equals(existingOptimizerType.getConnectionTypeA()) ||
                                connectionType.equals(existingOptimizerType.getConnectionTypeB()));
            }).collect(Collectors.toList());

            assetConnectionRepository.deleteAll(expiredConnections);
        }

        if(optimizerType != null) {
            if(domainA != null && optimizerType.getConnectionTypeA() != null){
                Asset assetA = assetRepository.findById(domainA).orElse(null);
                AssetConnection assetConnection = new AssetConnection();
                assetConnection.setAsset(assetA);
                assetConnection.setConnectedAsset(asset);
                assetConnection.setConnectionType(optimizerType.getConnectionTypeA());
                assetConnectionRepository.save(assetConnection);
            }
            if(domainB != null && optimizerType.getConnectionTypeB() != null){
                Asset assetB = assetRepository.findById(domainB).orElse(null);
                AssetConnection assetConnection = new AssetConnection();
                assetConnection.setAsset(assetB);
                assetConnection.setConnectedAsset(asset);
                assetConnection.setConnectionType(optimizerType.getConnectionTypeB());
                assetConnectionRepository.save(assetConnection);
            }
        }

        setAssetDetail(asset, "optimizer_type", aggregationConfigurationDAO.getMvoComponentType().getType());
        setAssetDetail(asset, "domain_a", domainA == null ? "" : domainA.toString());
        setAssetDetail(asset, "domain_b", domainB == null ? "" : domainB.toString());

        List<MeasurementAggregation> measurementAggregations = new ArrayList<>();
        List<List<MeasurementTags>> measurementTags = new ArrayList<>();
        for(MeasurementAggregationDAO measurementAggregationDAO : aggregationConfigurationDAO.getMeasurementAggregations()){
            //TODO: Check if we need to store the optimizer type and domains in measurement ??
            MeasurementAggregation measurementAggregation = new MeasurementAggregation();

            List<Measurement> aggregatedMeasurements = measurementRepository.findByIds(measurementAggregationDAO.getMeasurements());
            checkMeasurementsValidity(aggregatedMeasurements, measurementAggregationDAO.getMeasurements());
            measurementAggregation.setAggregatedMeasurements(new HashSet<>(aggregatedMeasurements));
            Measurement ref = aggregatedMeasurements.get(0);
            measurementTags.add(measurementTagsRepository.findByMeasurementId(ref.getId()));

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
                //TODO: This remains in details !!
                if(measurement.getDetails() == null)
                    measurement.setDetails(new ArrayList<>());
                measurement.getDetails().add(new MeasurementDetails("aggregation_type", x.getAggregationType(), null));
                measurement.getDetails().add(new MeasurementDetails("time_min", x.getTimeMin(), null));
                measurement.getDetails().add(new MeasurementDetails("time_max", x.getTimeMax(), null));
                measurement.getDetails().add(new MeasurementDetails("time_range", x.getTimeRange(), null));
                measurement.setUuid(uuidRepository.saveAndFlush(new UUID()));
                return measurement;
            }).collect(Collectors.toSet()));

            if(aggregationConfigurationDAO.getMvoComponentType().getDomainA() != null)
                measurementAggregation.setDomainA(aggregationConfigurationDAO.getMvoComponentType().getDomainA());
            else
                measurementAggregation.setDomainA(null);

            if(aggregationConfigurationDAO.getMvoComponentType().getDomainB() != null)
                measurementAggregation.setDomainB(aggregationConfigurationDAO.getMvoComponentType().getDomainB());
            else
                measurementAggregation.setDomainB(null);

            measurementAggregations.add(measurementAggregation);
        }

        assetDetailsRepository.deleteAll(asset.getDetails().stream().filter(x -> x.getKey().contains("_aggregation")).collect(Collectors.toList()));
        asset.getDetails().removeIf(x -> x.getKey().contains("_aggregation"));

        //TODO: Here process the params aggregation + set the value in the related assets
        //TODO: Check that the asset id is effectively part of the aggregation group,
        asset.getDetails().addAll(aggregationConfigurationDAO.getParameterAggregationConfigurations().stream()
                .filter(e -> e.get("aggregation") != null)
                .map(e -> new AssetDetails(e.get("parameter").toString()+"_aggregation",
                        e.get("aggregation").toString(), asset))
                .collect(Collectors.toList()));

        List<Asset> connectedAssets = asset.getConnections().stream()
                .map(AssetConnection::getConnectedAsset).collect(Collectors.toList());
        List<AssetDetails> assetDetailsToSave = new ArrayList<>();
        for(Map<String, Object> map : aggregationConfigurationDAO.getParameterAggregationConfigurations()){
            String key = (String) map.getOrDefault("parameter", null);
            if(key != null){
                for(Asset lookUpAsset : connectedAssets){
                    String value = (String) map.getOrDefault(lookUpAsset.getId().toString(), null);
                    AssetDetails assetDetails = assetDetailsRepository.findByKeyAndAssetId(key, lookUpAsset.getId())
                            .orElse(new AssetDetails(key, value, lookUpAsset));
                    assetDetails.setValue(value);
                    assetDetailsToSave.add(assetDetails);
                }
            }
        }
        assetDetailsRepository.saveAll(assetDetailsToSave);

        assetDetailsRepository.saveAll(asset.getDetails());
        assetRepository.save(asset);
        List<MeasurementAggregation> toDelete = measurementAggregationRepository.findByOutputMeasurementsAsset(asset);
        toDelete.forEach(x -> x.getOutputMeasurements().forEach(o -> {
            List<MeasurementTags> tags = measurementTagsRepository.findByMeasurementId(o.getId());
            tags.forEach(m -> m.getMeasurements().remove(o));
            measurementTagsRepository.saveAll(tags);
            measurementDetailsRepository.deleteAll(o.getDetails());
        }));
        measurementAggregationRepository.deleteAll(toDelete);
        measurementAggregationRepository.saveAll(measurementAggregations);
        for(int i = 0; i < measurementAggregations.size(); i++){
            Set<Measurement> measurements = measurementAggregations.get(0).getOutputMeasurements();
            List<MeasurementTags> tags = measurementTags.get(0);
            tags.forEach(x -> x.getMeasurements().addAll(measurements));
            measurementTagsRepository.saveAll(tags);
        }

        //TODO: Check how to overcome this.
        measurementAggregations.forEach(ma -> {
            ma.getOutputMeasurements().forEach(om -> {
                om.getDetails().forEach(d -> {
                    d.setMeasurement(om);
                    measurementDetailsRepository.save(d);
                });
            });
        });

        return get(assetId);
    }

    public List<OptimizerTypeDAO> getOptimizerTypeList() {
        return optimizerTypeRepository.findAll().stream().map(OptimizerTypeDAO::create).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getParametersForOptimizerType(Long assetId, String type) {
        Asset asset = assetRepository.findById(assetId).orElseThrow(NotFoundException::new);
        OptimizerType optimizerType = optimizerTypeRepository.findByName(type);

        return AggregationConfigurationDAO.createParameterAggregationConfiguration(asset, optimizerType, optimizerParameterRepository.findAll());
    }

    public List<MeasurementAggregationMeasurementSelectionDAO> getMeasurementsForAssetConnections(Long assetId){
        //TODO: Optimize this later.
        List<Long> ids = assetRepository.findById(assetId).orElseThrow(NotFoundException::new).getConnections()
                .stream().map(x -> x.getConnectedAsset().getId()).collect(Collectors.toList());
        List<Measurement> measurements = measurementRepository.findAll().stream()
                .filter(x -> x.getAsset() != null && ids.contains(x.getAsset().getId())).collect(Collectors.toList());

        return measurements.stream().map(MeasurementAggregationMeasurementSelectionDAO::create).collect(Collectors.toList());
    }

    public List<MeasurementAggregationMeasurementSelectionDAO> getMeasurementsForAssetConnectionsCompatible(Long assetId,
                                                                                                            Long selectedMeasurementId){
        Measurement refMeasurement = measurementRepository.findById(selectedMeasurementId).orElseThrow(NotFoundException::new);
        Map<String, String> tags = new HashMap<>();
        measurementTagsRepository.findByMeasurementId(refMeasurement.getId()).forEach(x -> tags.put(x.getKey(), x.getValue()));
        //TODO: Optimize this later.
        List<Long> ids = assetRepository.findById(assetId).orElseThrow(NotFoundException::new).getConnections()
                .stream().map(x -> x.getConnectedAsset().getId()).collect(Collectors.toList());
        List<Measurement> measurements = measurementRepository.findAll().stream()
                .filter(x -> {
                                    if(x.getAsset() != null
                                    && ids.contains(x.getAsset().getId())
                                    && Objects.equals(x.getDirection(), refMeasurement.getDirection())
                                    && Objects.equals(x.getDomain(), refMeasurement.getDomain())
                                    && Objects.equals(x.getName(), refMeasurement.getName())
                                    && Objects.equals(x.getSensorName(), refMeasurement.getSensorName())
                                    && Objects.equals(x.getSensorId(), refMeasurement.getSensorId())){
                                        List<String> remainingKeys = new ArrayList<>(tags.keySet());
                                        for(Details detail : measurementTagsRepository.findByMeasurementId(x.getId())){
                                            if((!tags.containsKey(detail.getKey()) || !remainingKeys.remove(detail.getKey()))
                                                    && Objects.equals(detail.getValue(),  tags.get(detail.getKey()))){
                                                return false;
                                            }
                                        }
                                        return true;
                                    }
                                    return false;
                        }

                ).collect(Collectors.toList());

        return measurements.stream().map(MeasurementAggregationMeasurementSelectionDAO::create).collect(Collectors.toList());
    }

    private void checkMeasurementsValidity(List<Measurement> measurements, List<Long> ids){
        Measurement ref = null;
        //TODO: Check for tags instead !!
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
                if(details.containsKey(x.getKey()) && remainingKeys.remove(x.getKey())) {
                    String value = details.get(x.getKey());
                    if(!Objects.equals(value, x.getValue()))
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
