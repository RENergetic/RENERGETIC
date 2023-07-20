package com.renergetic.ruleevaluationservice.validator;

import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.ruleevaluationservice.exception.validator.MissingMember;
import com.renergetic.ruleevaluationservice.exception.validator.ValidatorException;
import com.renergetic.ruleevaluationservice.model.AssetRule;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AssetRuleValidator {
    public void validate(AssetRule assetRule) throws ValidatorException {
        if(assetRule.getAsset() == null)
            throw new MissingMember("Asset link is mandatory but missing.");

        if(assetRule.getMeasurement1() == null)
            throw new MissingMember("First measurement is mandatory but missing.");
        if(assetRule.getFunctionMeasurement1() == null || assetRule.getTimeRangeMeasurement1() == null)
            throw new MissingMember("First measurement needs mandatory function and time range but are missing.");

        if(assetRule.getMeasurement2() != null){
            if(assetRule.getFunctionMeasurement2() == null || assetRule.getTimeRangeMeasurement2() == null)
                throw new MissingMember("Second measurement needs mandatory function and time range but are missing.");
        }
        else if(assetRule.isCompareToConfigThreshold()){
            Optional<AssetDetails> assetDetails = assetRule.getAsset().getDetails().stream().filter(ad -> ad.getKey().equals("rule_threshold")).findFirst();
            if(assetDetails.isEmpty() || assetDetails.get().getValue() == null || assetDetails.get().getValue().isEmpty())
                throw new MissingMember("threshold configuration not found for the asset. Make sure that there is a 'rule_threshold' key set with a value.");
        }
        else if(assetRule.getManualThreshold() != null){
            //No more checks to do for now.
        }
        else
            throw new MissingMember("You need either a second measurement, a threshold from the asset details or a manual threshold.");

        if(assetRule.getComparator() == null || assetRule.getComparator().isEmpty())
            throw new MissingMember("Comparator is mandatory.");
    }
}
