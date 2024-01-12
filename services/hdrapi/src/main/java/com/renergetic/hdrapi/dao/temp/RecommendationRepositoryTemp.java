//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.renergetic.hdrapi.dao.temp;

import com.renergetic.common.model.HDRRecommendation;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecommendationRepositoryTemp extends JpaRepository<HDRRecommendation, Long> {

    @Query("SELECT hdr FROM HDRRecommendation hdr WHERE hdr.timestamp = :timestamp and hdr.tag.id = :tagId ")
    Optional<HDRRecommendation> findByTimestampTag(LocalDateTime timestamp, Long tagId);
}
