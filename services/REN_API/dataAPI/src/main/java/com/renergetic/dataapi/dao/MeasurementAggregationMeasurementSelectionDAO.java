package com.renergetic.dataapi.dao;

import com.renergetic.common.model.Measurement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeasurementAggregationMeasurementSelectionDAO {
    private Long id;
    private String label;

    public static MeasurementAggregationMeasurementSelectionDAO create(Measurement measurement) {
        MeasurementAggregationMeasurementSelectionDAO mamsd = new MeasurementAggregationMeasurementSelectionDAO();
        mamsd.setId(measurement.getId());

        StringBuilder sb = new StringBuilder();
        if(measurement.getAsset() != null)
            sb.append("Asset ").append(measurement.getAsset().getId()).append(" - ");
        if(measurement.getLabel() != null)
            sb.append(measurement.getLabel());

        mamsd.setLabel(sb.toString());
        return mamsd;
    }
}
