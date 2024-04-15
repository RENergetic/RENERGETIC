package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.AssetRuleDAO;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.AssetRule;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.AssetRuleRepository;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.hdrapi.validator.AssetRuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetRuleService {
    @Autowired
    private AssetRuleRepository assetRuleRepository;
    @Autowired
    private AssetRuleValidator assetRuleValidator;

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private MeasurementRepository measurementRepository;

    public List<AssetRuleDAO> getAllRulesAssetForAssetId(Long id){
        List<AssetRule> assetRules = assetRuleRepository.findByAssetId(id);
        return assetRules.stream().map(AssetRuleDAO::fromEntity).collect(Collectors.toList());
    }

    public AssetRuleDAO getAssetRuleById(Long id) throws NotFoundException {
        Optional<AssetRule> assetRule = assetRuleRepository.findById(id);
        if(assetRule.isEmpty())
            throw new NotFoundException();
        return AssetRuleDAO.fromEntity(assetRule.get());
    }

    public List<AssetRuleDAO> createBatchAssetRule(List<AssetRuleDAO> assetRuleDAOs){
        return assetRuleRepository.saveAll(
                assetRuleDAOs.stream().map(this::extractEntityWithDependencies).collect(Collectors.toList()))
                .stream().map(AssetRuleDAO::fromEntity).collect(Collectors.toList());
    }

    public AssetRuleDAO createAssetRule(AssetRuleDAO assetRuleDAO){
        return AssetRuleDAO.fromEntity(assetRuleRepository.save(extractEntityWithDependencies(assetRuleDAO)));
    }

    public List<AssetRuleDAO> updateAndCreateBatchAssetRule(List<AssetRuleDAO> assetRuleDAOs){
        return assetRuleRepository.saveAll(
                        assetRuleDAOs.stream().map(this::extractEntityWithDependencies).collect(Collectors.toList()))
                .stream().map(AssetRuleDAO::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public List<AssetRuleDAO> updateAndCreateBatchAssetRuleForAssetId(Long assetId, List<AssetRuleDAO> assetRuleDAOs){
        List<AssetRule> assetRules = assetRuleRepository.findByAssetId(assetId);
        List<AssetRule> assetRulesFromClient = assetRuleDAOs.stream().map(this::extractEntityWithDependencies).collect(Collectors.toList());
        List<Long> existingIds = assetRulesFromClient.stream().map(AssetRule::getId).filter(Objects::nonNull).collect(Collectors.toList());
        assetRules = assetRules.stream().filter(x -> !existingIds.contains(x.getId())).collect(Collectors.toList());
        assetRuleRepository.deleteAll(assetRules);

        return assetRuleRepository.saveAll(assetRulesFromClient)
                .stream().map(AssetRuleDAO::fromEntity).collect(Collectors.toList());
    }

    public AssetRuleDAO updateAssetRule(AssetRuleDAO assetRuleDAO){
        if(!assetRuleRepository.existsById(assetRuleDAO.getId()))
            throw new NotFoundException("Asset rule id does not exist.");

        return AssetRuleDAO.fromEntity(assetRuleRepository.save(extractEntityWithDependencies(assetRuleDAO)));
    }

    public Boolean deleteAssetRule(Long id){
        if(!assetRuleRepository.existsById(id))
            throw new NotFoundException("Asset rule id does not exist.");

        assetRuleRepository.deleteById(id);
        return true;
    }

    private AssetRule extractEntityWithDependencies(AssetRuleDAO assetRuleDAO){
        Asset asset = null;
        Measurement measurement1 = null;
        Measurement measurement2 = null;
        Asset demandAssetTrue = null;
        Asset demandAssetFalse = null;
        if(assetRuleDAO.getAssetId() != null)
            asset = assetRepository.findById(assetRuleDAO.getAssetId()).orElseThrow(() -> new NotFoundException("Asset id not found."));
        if(assetRuleDAO.getMeasurement1Id() != null)
            measurement1 = measurementRepository.findById(assetRuleDAO.getMeasurement1Id()).orElseThrow(() -> new NotFoundException("Measurement1 id not found."));
        if(assetRuleDAO.getMeasurement2Id() != null)
            measurement2 = measurementRepository.findById(assetRuleDAO.getMeasurement2Id()).orElseThrow(() -> new NotFoundException("Measurement2 id not found."));
        if(assetRuleDAO.getDemandAssetTrue() != null)
            demandAssetTrue = assetRepository.findById(assetRuleDAO.getDemandAssetTrue()).orElseThrow(() -> new NotFoundException("Asset demand true id not found."));
        if(assetRuleDAO.getDemandAssetFalse() != null)
            demandAssetFalse = assetRepository.findById(assetRuleDAO.getDemandAssetFalse()).orElseThrow(() -> new NotFoundException("Asset demand false id not found."));
        AssetRule assetRule = assetRuleDAO.mapToEntity(asset, measurement1, measurement2, demandAssetTrue, demandAssetFalse);
        assetRuleValidator.validate(assetRule);
        return assetRule;
    }
}
