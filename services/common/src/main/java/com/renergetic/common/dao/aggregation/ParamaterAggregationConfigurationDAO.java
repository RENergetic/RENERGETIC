package com.renergetic.common.dao.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.AssetConnection;
import com.renergetic.common.model.OptimizerParameter;
import com.renergetic.common.model.OptimizerType;
import com.renergetic.common.model.details.AssetDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class ParamaterAggregationConfigurationDAO {
    @JsonProperty(value = "required", required = true)
    private Boolean required;
    @JsonProperty(value = "aggregation", required = true)
    private String aggregation;
    @JsonIgnore
    private Map<Long, Double> assetsValues;

    public static Map<String, ParamaterAggregationConfigurationDAO> create(Asset asset, OptimizerType optimizerType){
        Map<String, ParamaterAggregationConfigurationDAO> map = new HashMap<>();
        List<Asset> connectedAssets = asset.getConnections().stream()
                .map(AssetConnection::getConnectedAsset).collect(Collectors.toList());

        for(OptimizerParameter optimizerParameter : optimizerType.getOptimizerParameters()){
            ParamaterAggregationConfigurationDAO config = new ParamaterAggregationConfigurationDAO();
            config.setRequired(optimizerParameter.isRequired());
            config.setAggregation(asset.getDetails().stream()
                    .filter(x -> x.getKey().equals(optimizerParameter.getParameterName()+"_aggregation"))
                    .findFirst().orElse(new AssetDetails()).getValue());

            Map<Long, Double> values = new HashMap<>();
            for(Asset connectedAsset : connectedAssets){
                AssetDetails assetDetails = connectedAsset.getDetails().stream()
                        .filter(x -> x.getKey().equals(optimizerParameter.getParameterName()+"_aggregation"))
                        .findFirst().orElse(null);
                values.put(connectedAsset.getId(), assetDetails == null ? null : Double.valueOf(assetDetails.getValue()));
            }
            config.setAssetsValues(values);

            map.put(optimizerParameter.getParameterName(), config);
        }
        return map;
    }
}
