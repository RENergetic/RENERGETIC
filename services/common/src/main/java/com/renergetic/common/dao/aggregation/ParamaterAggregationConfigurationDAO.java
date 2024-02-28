package com.renergetic.common.dao.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.AssetConnection;
import com.renergetic.common.model.OptimizerParameter;
import com.renergetic.common.model.OptimizerType;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.common.repository.OptimizerParameterRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Getter
@Setter
public class ParamaterAggregationConfigurationDAO {
    @JsonProperty(value = "required", required = true)
    private Boolean required;
    @JsonProperty(value = "aggregation", required = true)
    private String aggregation;
    @JsonProperty(value = "assetValues", required = false)
    private Map<Long, Double> assetsValues;

    public static Map<String, ParamaterAggregationConfigurationDAO> create(Asset asset, OptimizerType optimizerType,
                                                                           List<OptimizerParameter> allParameters){
        Map<String, ParamaterAggregationConfigurationDAO> map = new LinkedHashMap<>();

        if(optimizerType != null){
            List<Asset> connectedAssets = asset.getConnections().stream()
                    .map(AssetConnection::getConnectedAsset).collect(Collectors.toList());

            List<OptimizerParameter> parameterList = optimizerType.getOptimizerParameters();
            //Ordering by required/optional then name.
            parameterList.sort(Comparator.comparing(
                (p -> p), (p1, p2) -> {
                    if(p1.isRequired() != p2.isRequired())
                        return p1.isRequired() ? -1 : 1;

                    return p1.getParameterName().compareTo(p2.getParameterName());
                }));
            List<String> existingParams = parameterList.stream().map(OptimizerParameter::getParameterName).collect(Collectors.toList());
            for(OptimizerParameter optimizerParameter : parameterList){
                map.put(optimizerParameter.getParameterName(), createConfig(asset, connectedAssets, optimizerParameter, false));
            }
            allParameters = allParameters.stream().filter(x -> !existingParams.contains(x.getParameterName()))
                    .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(OptimizerParameter::getParameterName))),
                    ArrayList::new));
            allParameters.sort(Comparator.comparing(OptimizerParameter::getParameterName));
            for(OptimizerParameter optimizerParameter : allParameters){
                map.put(optimizerParameter.getParameterName(), createConfig(asset, connectedAssets, optimizerParameter, true));
            }
        }

        return map;
    }

    private static ParamaterAggregationConfigurationDAO createConfig(Asset asset, List<Asset> connectedAssets,
                                                                     OptimizerParameter optimizerParameter, boolean unused){
        ParamaterAggregationConfigurationDAO config = new ParamaterAggregationConfigurationDAO();
        config.setRequired(unused ? null : optimizerParameter.isRequired());
        config.setAggregation(asset.getDetails().stream()
                .filter(x -> x.getKey().equals(optimizerParameter.getParameterName()+"_aggregation"))
                .findFirst().orElse(new AssetDetails()).getValue());

        Map<Long, Double> values = new HashMap<>();
        for(Asset connectedAsset : connectedAssets){
            AssetDetails assetDetails = connectedAsset.getDetails().stream()
                    .filter(x -> x.getKey().equals(optimizerParameter.getParameterName()))
                    .findFirst().orElse(null);
            values.put(connectedAsset.getId(), assetDetails == null ? null : Double.valueOf(assetDetails.getValue()));
        }
        config.setAssetsValues(values);
        return config;
    }
}
