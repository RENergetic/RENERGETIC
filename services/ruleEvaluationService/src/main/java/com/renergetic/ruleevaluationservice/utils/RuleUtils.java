package com.renergetic.ruleevaluationservice.utils;

import com.renergetic.common.model.RuleDefinition;

public class RuleUtils {
    public static String transformRuleToReadableName(RuleDefinition ruleDefinition){
        StringBuilder sb = new StringBuilder();
        sb.append(ruleDefinition.getMeasurement1().getMultiplier()).append("*")
                .append(ruleDefinition.getMeasurement1().getFunction()).append("(id:")
                .append(ruleDefinition.getMeasurement1().getId()).append(",")
                .append(ruleDefinition.getMeasurement1().getRangeFrom()).append("->")
                .append(ruleDefinition.getMeasurement1().getRangeTo()).append(") ").append(ruleDefinition.getComparator())
                .append(" ");

        if(ruleDefinition.getMeasurement2() != null){
            sb.append(ruleDefinition.getMeasurement2().getMultiplier()).append("*")
                    .append(ruleDefinition.getMeasurement2().getFunction()).append("(id:")
                    .append(ruleDefinition.getMeasurement2().getId()).append(",")
                    .append(ruleDefinition.getMeasurement2().getRangeFrom()).append("->")
                    .append(ruleDefinition.getMeasurement2().getRangeTo()).append(")");
        } else {
            sb.append(ruleDefinition.getManualThreshold());
        }
        return sb.toString();
    }
}
