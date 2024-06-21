package com.renergetic.common.utilities;

import com.renergetic.common.model.details.AssetDetails;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AssetDetailsAggregation {
    public static String aggregate(String type, String key, List<AssetDetails> connectedDetails) {
        StringBuffer suffix = new StringBuffer(10);

        switch (type) {
            case "sum":
                return connectedDetails.stream().filter(x -> matchingKeyAndValidValue(x, key, suffix))
                        .map(AssetDetailsAggregation::getDouble).reduce(0.0, Double::sum) +
                        (suffix.isEmpty() ? "" : (" " + suffix));
            case "min":
                return connectedDetails.stream().filter(x -> matchingKeyAndValidValue(x, key, suffix))
                        .map(AssetDetailsAggregation::getDouble).mapToDouble(Double::doubleValue).min().orElse(0.0) +
                        (suffix.isEmpty() ? "" : (" " + suffix));
            case "max":
                return connectedDetails.stream().filter(x -> matchingKeyAndValidValue(x, key, suffix))
                        .map(AssetDetailsAggregation::getDouble).mapToDouble(Double::doubleValue).max().orElse(0.0) +
                        (suffix.isEmpty() ? "" : (" " + suffix));
            case "mean":
                return connectedDetails.stream().filter(x -> matchingKeyAndValidValue(x, key, suffix))
                        .map(AssetDetailsAggregation::getDouble).mapToDouble(Double::doubleValue).average().orElse(0.0) +
                        (suffix.isEmpty() ? "" : (" " + suffix));
        }
        return "";
    }

    public static boolean matchingKeyAndValidValue(AssetDetails assetDetails, String key, StringBuffer suffix) {
        String value = assetDetails.getValue();
        return assetDetails.getKey().equals(key) && value != null
                && containsSpaceAndNumeric(value, suffix);
    }

    public static boolean containsSpaceAndNumeric(String value, StringBuffer suffix){
        String[] split = value.split(" ");
        if(split.length > 2 || split.length == 0)
            return false;

        if(StringUtils.isNumeric(split[0])) {
            if(split.length == 2 && suffix.isEmpty())
                suffix.append(split[1]);
            return true;
        }

        return false;
    }

    public static Double getDouble(AssetDetails assetDetails) {
        String value = assetDetails.getValue();
        if(value == null)
            return Double.NaN;

        String[] split = value.split(" ");
        if(split.length > 2 || split.length == 0)
            return Double.NaN;

        if(StringUtils.isNumeric(split[0]))
            return Double.parseDouble(split[0]);
        return Double.NaN;
    }
}
