package com.renergetic.common.dao.aggregation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class AggregationConfigurationDAO {
    @JsonProperty(value = "measurementAggregation", required = true)
    private List<MeasurementAggregationDAO> measurementAggregations;
    @JsonProperty(value = "mvoComponentType", required = true)
    private MuVeCoTypeDAO mvoComponentType;
    @JsonProperty(value = "parametersAggregationConfiguration", required = true)
    private Map<String, ParamaterAggregationConfigurationDAO> parameterAggregationConfigurations;

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
        aggregationConfigurationDAO.setParameterAggregationConfigurations(
                ParamaterAggregationConfigurationDAO.create(asset, optimizerType, allParameters));

        return aggregationConfigurationDAO;
    }
}
