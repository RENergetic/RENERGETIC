package com.renergetic.hdrapi.dao.temp;

import com.renergetic.common.model.Asset;
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


    @Query(value = "SELECT DISTINCT `timestamp`  from hdr_recommendation where COALESCE(`timestamp` >= :timestamp, TRUE) order by `timestamp` desc", nativeQuery = true)
    List<LocalDateTime> listRecentRecommendations(LocalDateTime timestamp);

    @Query(value = "DELETE hdr_recommendation   WHERE hdr_recommendation.timestamp = :timestamp ", nativeQuery = true)
    void deleteByTimestamp(LocalDateTime timestamp);

    @Query("SELECT hdr FROM HDRRecommendation hdr   WHERE hdr.timestamp = :timestamp ")
    List<HDRRecommendation> findByTimestamp(LocalDateTime timestamp);

    @Query("SELECT hdr FROM HDRRecommendation hdr WHERE hdr.timestamp = :timestamp and hdr.tag.key = :tagId ")
    Optional<HDRRecommendation> findByTimestampTag(LocalDateTime timestamp, Long tagId);


}
