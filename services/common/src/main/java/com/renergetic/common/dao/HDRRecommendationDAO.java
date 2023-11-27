package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.dao.details.MeasurementTagsDAO;
import com.renergetic.common.model.HDRRecommendation;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HDRRecommendationDAO {

    //TODO: unique key: name-asset-sensor-type-direction
    @JsonProperty(required = true)
    private LocalDateTime timestamp;
    @JsonProperty(required = false)
    private MeasurementTagsDAO tag;
    @JsonProperty(required = false)
    private String label;

    public static HDRRecommendationDAO create(HDRRecommendation recommendation) {

        HDRRecommendationDAO dao = null;

        if (recommendation != null) {
            dao = new HDRRecommendationDAO();
            dao.setTimestamp(recommendation.getTimestamp());
            dao.setLabel(recommendation.getLabel());
            dao.setTag(MeasurementTagsDAO.create(recommendation.getTag()));
        }
        return dao;
    }

    public HDRRecommendation mapToEntity() {
        HDRRecommendation recommendation = new HDRRecommendation();
        recommendation.setTag(this.getTag().mapToEntity());
        recommendation.setLabel(this.getLabel());
        return recommendation;
    }

}
