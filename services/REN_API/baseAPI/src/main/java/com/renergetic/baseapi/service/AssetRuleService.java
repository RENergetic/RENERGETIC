package com.renergetic.baseapi.service;

import com.renergetic.common.dao.RuleDAO;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.baseapi.service.validator.AssetRuleValidator;
import com.renergetic.common.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetRuleService {
    @Autowired
    private RuleRepository ruleRepository;
    @Autowired
    private AssetRuleValidator assetRuleValidator;

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private MeasurementRepository measurementRepository;

    public List<RuleDAO> getAllRulesAsset(){
        return ruleRepository.findByRootTrue().stream().map(RuleDAO::fromEntity).collect(Collectors.toList());
    }

    public List<RuleDAO> createUpdateDeleteRules(List<RuleDAO> createUpdate, List<Long> delete){
        //TODO: Reuse AssetRuleValidator
        ruleRepository.deleteAllById(delete);
        return ruleRepository.saveAll(createUpdate.stream().map(RuleDAO::mapToEntity).collect(Collectors.toList()))
                .stream().map(RuleDAO::fromEntity).collect(Collectors.toList());
    }
}
