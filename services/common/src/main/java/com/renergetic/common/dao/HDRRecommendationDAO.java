package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.dao.details.MeasurementTagsDAO;
import com.renergetic.common.model.HDRRecommendation;
import com.renergetic.common.utilities.DateConverter;
import lombok.Data;

@Data
public class HDRRecommendationDAO {

    //todo: unique key: name-asset-sensor-type-direction
    @JsonProperty(required = false)
    private Long id;
    @JsonProperty(required = true)
    private Long timestamp;
    @JsonProperty(required = true)
    private MeasurementTagsDAO tag;
    @JsonProperty(required = false)
    private String label;

    public static HDRRecommendationDAO create(HDRRecommendation recommendation) {

        HDRRecommendationDAO dao = null;

        if (recommendation != null) {
            dao = new HDRRecommendationDAO();
            dao.setTimestamp(DateConverter.toEpoch(recommendation.getTimestamp()));
            dao.setLabel(recommendation.getLabel());
            dao.setTag(MeasurementTagsDAO.create(recommendation.getTag()));
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
