package com.renergetic.ruleevaluationservice.service;

import com.renergetic.ruleevaluationservice.dao.AssetRuleDAO;
import com.renergetic.ruleevaluationservice.repository.AssetRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetRuleService {
    @Autowired
    private AssetRuleRepository assetRuleRepository;
    @Autowired
    private
    public List<AssetRuleDAO> getAllRulesAssetForAssetId(Long id){

    }

    public AssetRuleDAO getAssetRuleById(Long id){

    }

    public List<AssetRuleDAO> createBatchAssetRule(List<AssetRuleDAO> assetRuleDAOs){
        List<AssetRuleDAO> output = new ArrayList<>();
        for(AssetRuleDAO assetRule : assetRuleDAOs)
            output.add(createAssetRule(assetRule));
        return output;
    }

    public AssetRuleDAO createAssetRule(AssetRuleDAO assetRuleDAO){
        //TODO: Validate.
    }

    public List<AssetRuleDAO> updateBatchAssetRule(List<AssetRuleDAO> assetRuleDAOs){
        List<AssetRuleDAO> output = new ArrayList<>();
        for(AssetRuleDAO assetRule : assetRuleDAOs)
            output.add(updateAssetRule(assetRule));
        return output;
    }

    public AssetRuleDAO updateAssetRule(AssetRuleDAO assetRuleDAO){
        //TODO: Validate.
    }

    public Boolean deletAssetRule(Long id){

    }
}
