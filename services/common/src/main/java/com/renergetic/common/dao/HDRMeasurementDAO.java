package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.dao.details.MeasurementTagsDAO;
import com.renergetic.common.model.HDRRecommendation;
import com.renergetic.common.utilities.DateConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class HDRMeasurementDAO {

    //todo: unique key: name-asset-sensor-type-direction
    @JsonProperty(required = false)
    private Long id;
    @JsonProperty(required = true)
    private Long timestamp;
    @JsonProperty(required = true)
    private MeasurementDAOResponse measurement;

    @JsonProperty(required = false)
    private String label;

    public static HDRMeasurementDAO create(HDRRecommendation recommendation) {

        HDRMeasurementDAO dao = null;

        if (recommendation != null) {
            dao = new HDRMeasurementDAO();
            dao.setTimestamp(DateConverter.toEpoch(recommendation.getTimestamp()));
            dao.setLabel(recommendation.getLabel());
            dao.set(MeasurementDAOResponse.create());
            dao.setId(recommendation.getId());
        }
        return dao;
    }

    public HDRRecommendation mapToEntity() {
        HDRRecommendation recommendation = new HDRRecommendation();
        recommendation.setTag(this.getTag().mapToEntity());
        recommendation.setLabel(this.getLabel());
        if (this.id != null)
            recommendation.setId(this.id);
        recommendation.setTimestamp(DateConverter.toLocalDateTime(this.getTimestamp()));
        return recommendation;
    }

}
