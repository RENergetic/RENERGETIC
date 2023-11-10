//package com.renergetic.hdrapi.dao.temp;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//import javax.persistence.Column;
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@RequiredArgsConstructor
//@ToString
//public class HDRRecommendationDAO {
//
//    //todo: unique key: name-asset-sensor-type-direction
//    @JsonProperty(required = true)
//    private LocalDateTime timestamp;
//    @JsonProperty(required = false)
//    private MeasurementTagsDAO tag;
//    @Column(name = "label")
//    private String label;
//
//    public static HDRRecommendationDAO create(HDRRecommendation recommendation) {
//
//        HDRRecommendationDAO dao = null;
//
//        if (recommendation != null) {
//            dao = new HDRRecommendationDAO();
//            dao.setTimestamp(recommendation.getTimestamp());
//            dao.setLabel(recommendation.getLabel());
//            dao.setTag(MeasurementTagsDAO.create(recommendation.getTag()));
//        }
//        return dao;
//    }
//
//    public HDRRecommendation mapToEntity() {
//        HDRRecommendation recommendation = new HDRRecommendation();
//        recommendation.setTag(this.getTag().mapToEntity());
//        recommendation.setLabel(this.getLabel());
//        return recommendation;
//    }
//
//}
