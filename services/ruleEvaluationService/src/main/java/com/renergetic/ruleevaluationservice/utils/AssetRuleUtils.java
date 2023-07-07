package com.renergetic.ruleevaluationservice.utils;

import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.ruleevaluationservice.model.AssetRule;

public class AssetRuleUtils {
    public static String transformRuleToReadableName(AssetRule assetRule){
        StringBuilder sb = new StringBuilder();
        sb.append(assetRule.getFunctionMeasurement1()).append("(id:")
                .append(assetRule.getMeasurement1().getId()).append(",")
                .append(assetRule.getTimeRangeMeasurement1()).append(") ").append(assetRule.getComparator())
                .append(" ");

        if(assetRule.getMeasurement2() != null){
            sb.append(assetRule.getFunctionMeasurement2()).append("(id:")
                    .append(assetRule.getMeasurement2().getId()).append(",")
                    .append(assetRule.getTimeRangeMeasurement2()).append(")");
        } else {
            if(assetRule.isCompareToConfigThreshold()){
                //TODO: Handle exception here.
                AssetDetails assetDetails = assetRule.getAsset().getDetails().stream().filter(ad -> ad.getKey().equals("rule_threshold")).findFirst().orElseThrow();
                sb.append(assetDetails.getValue());
            } else {
                sb.append(assetRule.getManualThreshold());
            }
        }
        return sb.toString();
    }
}
