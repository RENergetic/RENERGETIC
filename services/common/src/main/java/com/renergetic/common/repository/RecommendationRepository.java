package com.renergetic.common.repository;

import com.renergetic.common.model.Asset;
import com.renergetic.common.model.HDRRecommendation;
import com.renergetic.common.model.Heatmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface RecommendationRepository extends JpaRepository<HDRRecommendation, Long> {


    HDRRecommendation save(HDRRecommendation recommendation);

    @Query(value = "SELECT max(`timestamp`) as `timestamp` from hdr_recommendation", nativeQuery = true)
    Optional<LocalDateTime> getRecentRecommendation();

    @Query(value = "DELETE hdr_recommendation WHERE hdr_recommendation.timestamp = :timestamp ", nativeQuery = true)
    Optional<LocalDateTime> deleteByTimestamp(LocalDateTime timestamp);

    @Query("SELECT hdr FROM HDRRecommendation hdr WHERE hdr.timestamp = :timestamp ")
    List<HDRRecommendation> findByTimestamp(LocalDateTime timestamp);

    @Query("SELECT hdr FROM HDRRecommendation hdr WHERE hdr.timestamp = :timestamp and hdr.tag.key = :tagId ")
    Optional<HDRRecommendation> findByTimestampTag(LocalDateTime timestamp, Long tagId);
}
