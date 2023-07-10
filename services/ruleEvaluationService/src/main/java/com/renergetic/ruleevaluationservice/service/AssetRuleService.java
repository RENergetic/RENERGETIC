package com.renergetic.ruleevaluationservice.service;

import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.ruleevaluationservice.dao.AssetRuleDAO;
import com.renergetic.ruleevaluationservice.model.AssetRule;
import com.renergetic.ruleevaluationservice.repository.AssetRuleRepository;
import com.renergetic.ruleevaluationservice.validator.AssetRuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        List<AssetRuleDAO> output = new ArrayList<>();
        for(AssetRuleDAO assetRule : assetRuleDAOs)
            output.add(createAssetRule(assetRule));
        return output;
    }

    public AssetRuleDAO createAssetRule(AssetRuleDAO assetRuleDAO){
        Asset asset = null;
        Measurement measurement1 = null;
        Measurement measurement2 = null;
        if(assetRuleDAO.getAssetId() != null)
            asset = assetRepository.findById(assetRuleDAO.getAssetId()).orElseThrow(() -> new NotFoundException("Asset id not found."));
        if(assetRuleDAO.getMeasurement1Id() != null)
            measurement1 = measurementRepository.findById(assetRuleDAO.getMeasurement1Id()).orElseThrow(() -> new NotFoundException("Measurement1 id not found."));
        if(assetRuleDAO.getMeasurement2Id() != null)
            measurement2 = measurementRepository.findById(assetRuleDAO.getMeasurement2Id()).orElseThrow(() -> new NotFoundException("Measurement2 id not found."));
        AssetRule assetRule = assetRuleDAO.mapToEntity(asset, measurement1, measurement2);
        assetRuleValidator.validate(assetRule);
        return AssetRuleDAO.fromEntity(assetRuleRepository.save(assetRule));
    }

    public List<AssetRuleDAO> updateBatchAssetRule(List<AssetRuleDAO> assetRuleDAOs){
        List<AssetRuleDAO> output = new ArrayList<>();
        for(AssetRuleDAO assetRule : assetRuleDAOs)
            output.add(updateAssetRule(assetRule));
        return output;
    }

    public AssetRuleDAO updateAssetRule(AssetRuleDAO assetRuleDAO){
        if(!assetRuleRepository.existsById(assetRuleDAO.getId()))
            throw new NotFoundException("Asset rule id does not exist.");

        Asset asset = null;
        Measurement measurement1 = null;
        Measurement measurement2 = null;
        if(assetRuleDAO.getAssetId() != null)
            asset = assetRepository.findById(assetRuleDAO.getAssetId()).orElseThrow(() -> new NotFoundException("Asset id not found."));
        if(assetRuleDAO.getMeasurement1Id() != null)
            measurement1 = measurementRepository.findById(assetRuleDAO.getMeasurement1Id()).orElseThrow(() -> new NotFoundException("Measurement1 id not found."));
        if(assetRuleDAO.getMeasurement2Id() != null)
            measurement2 = measurementRepository.findById(assetRuleDAO.getMeasurement2Id()).orElseThrow(() -> new NotFoundException("Measurement2 id not found."));
        AssetRule assetRule = assetRuleDAO.mapToEntity(asset, measurement1, measurement2);
        assetRuleValidator.validate(assetRule);
        return AssetRuleDAO.fromEntity(assetRuleRepository.save(assetRule));
    }

    public Boolean deleteAssetRule(Long id){
        if(!assetRuleRepository.existsById(id))
            throw new NotFoundException("Asset rule id does not exist.");

        assetRuleRepository.deleteById(id);
        return true;
    }
}
