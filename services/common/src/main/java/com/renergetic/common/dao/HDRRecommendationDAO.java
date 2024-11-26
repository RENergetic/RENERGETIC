package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.dao.details.MeasurementTagsDAO;
import com.renergetic.common.dao.details.TagDAO;
import com.renergetic.common.model.HDRRecommendation;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.common.utilities.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.json.ParseException;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class HDRRecommendationDAO {

    // TODO: unique key: name-asset-sensor-type-direction
    @JsonProperty(required = false)
    private Long id;
    @JsonProperty(required = true)
    private Long timestamp;
    @JsonProperty(required = true)
    private TagDAO tag;
    @JsonProperty(required = false)
    private String label;
    @JsonProperty(required = false)
    private Long rank;
    @JsonProperty(required = false)
    private Map<String, ?> properties;

    public static HDRRecommendationDAO create(HDRRecommendation recommendation) {

        HDRRecommendationDAO dao = null;

        if (recommendation != null) {
            dao = new HDRRecommendationDAO();
            dao.setTimestamp(recommendation.getTimestamp());
            dao.setLabel(recommendation.getLabel());
            dao.setTag(TagDAO.create(recommendation.getTag()));
            dao.setId(recommendation.getId());
            dao.setRank(recommendation.getRank());
            if (recommendation.getProperties() != null && !recommendation.getProperties().isEmpty()) {
                try {
                    dao.setProperties(Json.parse(recommendation.getProperties()).toMap());
                } catch (ParseException e) {
                    //TODO: log error
                }
            }

        }
        return dao;
    }

    public HDRRecommendation mapToEntity() {
        HDRRecommendation recommendation = new HDRRecommendation();
        recommendation.setTag(this.getTag().mapToEntity());
        recommendation.setLabel(this.getLabel());
        recommendation.setRank(this.getRank());
        if (this.id != null)
            recommendation.setId(this.id);
        recommendation.setTimestamp(this.getTimestamp());
        if (this.properties != null && !this.properties.isEmpty()) {
            recommendation.setProperties(Json.toJson(this.properties));
        }
        return recommendation;
    }

}
