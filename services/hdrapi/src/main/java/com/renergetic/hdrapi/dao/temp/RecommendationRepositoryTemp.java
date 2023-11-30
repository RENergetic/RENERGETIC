package com.renergetic.hdrapi.dao.temp;

import com.renergetic.common.model.HDRRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface RecommendationRepositoryTemp extends JpaRepository<HDRRecommendation, Long> {



    @Query(value = "SELECT max(\"timestamp\") as \"timestamp\" from hdr_recommendation", nativeQuery = true)
    Optional<LocalDateTime> getRecentRecommendation();

    @Query(value = "SELECT DISTINCT \"timestamp\"  from hdr_recommendation where COALESCE(\"timestamp\" >= :timestamp, TRUE)" +
            " order by \"timestamp\" desc", nativeQuery = true)
    List<LocalDateTime> listRecentRecommendations(LocalDateTime timestamp);




}
