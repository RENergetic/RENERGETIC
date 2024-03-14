package com.renergetic.common.utilities;

import com.renergetic.common.model.details.AssetDetails;

import java.util.List;

public class AssetDetailsAggregation {
    public static String aggregate(String type, String key, List<AssetDetails> connectedDetails) {
        switch (type) {
            case "sum":
                return connectedDetails.stream().filter(x -> x.getKey().equals(key))
                        .map(x -> Double.parseDouble(x.getValue())).reduce(0.0, Double::sum).toString();
            case "min":
                return String.valueOf(connectedDetails.stream().filter(x -> x.getKey().equals(key))
                        .map(x -> Double.parseDouble(x.getValue())).mapToDouble(Double::doubleValue).min().orElse(0.0));
            case "max":
                return String.valueOf(connectedDetails.stream().filter(x -> x.getKey().equals(key))
                        .map(x -> Double.parseDouble(x.getValue())).mapToDouble(Double::doubleValue).max().orElse(0.0));
            case "mean":
                return String.valueOf(connectedDetails.stream().filter(x -> x.getKey().equals(key))
                        .map(x -> Double.parseDouble(x.getValue())).mapToDouble(Double::doubleValue).average().orElse(0.0));
        }
        return "";
    }
}
