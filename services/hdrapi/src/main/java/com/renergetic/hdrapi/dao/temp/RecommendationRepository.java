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
    Optional<LocalDateTime> getRecentRecommendation(Asset user);

    @Query("SELECT hdr FROM HDRRecommendation hdr   WHERE hdr.timestamp = :timestmap ")
    List<HDRRecommendation> findByTimestamp(LocalDateTime timestmap);
}
