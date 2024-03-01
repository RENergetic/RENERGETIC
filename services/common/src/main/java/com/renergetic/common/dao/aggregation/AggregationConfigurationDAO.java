package com.renergetic.common.dao.aggregation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.AssetDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Getter
@Setter
public class AggregationConfigurationDAO {
    @JsonProperty(value = "measurementAggregation", required = true)
    private List<MeasurementAggregationDAO> measurementAggregations;
    @JsonProperty(value = "mvoComponentType", required = true)
    private MuVeCoTypeDAO mvoComponentType;
    @JsonProperty(value = "parametersAggregationConfiguration", required = true)
    private List<Map<String, Object>> parameterAggregationConfigurations;
    @JsonProperty(value = "columns", required = false)
    private List<Map<String, String>> columns;

    public static AggregationConfigurationDAO create(Asset asset, List<MeasurementAggregation> measurementAggregations, OptimizerType optimizerType,
                                                     List<OptimizerParameter> allParameters){
        AggregationConfigurationDAO aggregationConfigurationDAO = new AggregationConfigurationDAO();
        aggregationConfigurationDAO.setMeasurementAggregations(MeasurementAggregationDAO.create(measurementAggregations));

        if(measurementAggregations != null && !measurementAggregations.isEmpty())
            aggregationConfigurationDAO.setMvoComponentType(MuVeCoTypeDAO
                .create(asset, optimizerType));
        else
            aggregationConfigurationDAO.setMvoComponentType(MuVeCoTypeDAO
                    .create(null, optimizerType));

        //TODO: in the caller, get the optimizer type based on the one selected and set in MuVeCoTypeDOA.
        /*aggregationConfigurationDAO.setParameterAggregationConfigurations(
                ParamaterAggregationConfigurationDAO.create(asset, optimizerType, allParameters));*/

        List<Asset> connectedAssets = asset.getConnections().stream()
                .map(AssetConnection::getConnectedAsset).collect(Collectors.toList());

        List<Map<String, String>> columns = new ArrayList<>();
        for(Asset connectAsset : connectedAssets){
            Map<String, String> map = new HashMap<>();
            map.put("key", connectAsset.getId().toString());
            map.put("label", connectAsset.getId().toString());
            columns.add(map);
        }
        aggregationConfigurationDAO.setColumns(columns);

        aggregationConfigurationDAO.setParameterAggregationConfigurations(
                createParameterAggregationConfiguration(asset, optimizerType, allParameters));

        return aggregationConfigurationDAO;
    }

    public static List<Map<String, Object>> createParameterAggregationConfiguration(Asset asset, OptimizerType optimizerType,
                                                                                    List<OptimizerParameter> allParameters){
        List<Map<String, Object>> params = new ArrayList<>();


        List<Asset> connectedAssets = asset.getConnections().stream()
                .map(AssetConnection::getConnectedAsset).collect(Collectors.toList());


        List<String> existingParams = new ArrayList<>();
        if(optimizerType != null){
            List<OptimizerParameter> parameterList = optimizerType.getOptimizerParameters();
            //Ordering by required/optional then name.
            parameterList.sort(Comparator.comparing(
                    (p -> p), (p1, p2) -> {
                        if(p1.isRequired() != p2.isRequired())
                            return p1.isRequired() ? -1 : 1;

                        return p1.getParameterName().compareTo(p2.getParameterName());
                    }));
            existingParams.addAll(parameterList.stream().map(OptimizerParameter::getParameterName).collect(Collectors.toList()));
            for(OptimizerParameter optimizerParameter : parameterList){
                params.add(generateObjectOfData(asset, connectedAssets, optimizerParameter, false));
            }
        }
        allParameters = allParameters.stream().filter(x -> !existingParams.contains(x.getParameterName()))
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(OptimizerParameter::getParameterName))),
                        ArrayList::new));
        allParameters.sort(Comparator.comparing(OptimizerParameter::getParameterName));
        for(OptimizerParameter optimizerParameter : allParameters){
            params.add(generateObjectOfData(asset, connectedAssets, optimizerParameter, true));
        }

        return params;
    }

    private static Map<String, Object> generateObjectOfData(Asset asset, List<Asset> connectedAssets,
                                                     OptimizerParameter optimizerParameter, boolean unused){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("aggregation",asset.getDetails().stream()
                .filter(x -> x.getKey().equals(optimizerParameter.getParameterName()+"_aggregation"))
                .findFirst().orElse(new AssetDetails()).getValue());
        map.put("parameter", optimizerParameter.getParameterName());
        map.put("required", unused ? null : optimizerParameter.isRequired());

        for(Asset connectedAsset : connectedAssets){
            AssetDetails assetDetails = connectedAsset.getDetails().stream()
                    .filter(x -> x.getKey().equals(optimizerParameter.getParameterName()))
                    .findFirst().orElse(null);
            map.put(connectedAsset.getId().toString(), assetDetails == null ? null : assetDetails.getValue());
        }

        return map;
    }
}
