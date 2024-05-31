package com.renergetic.common.repository;

import com.renergetic.common.model.HDRMeasurement;
import com.renergetic.common.model.HDRRecommendation;
import com.renergetic.common.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface HDRRecommendationRepository extends JpaRepository<HDRRecommendation, Long> {


    HDRRecommendation save(HDRRecommendation recommendation);

    @Query(value = "SELECT max(\"timestamp\") as \"timestamp\" from hdr_recommendation", nativeQuery = true)
    Optional<Long> getRecentRecommendation();

    @Query(
            value = "SELECT   \"timestamp\" from hdr_recommendation  where hdr_recommendation.timestamp = :timestamp",
            nativeQuery = true
    )
    Optional<Long> timestampExists(Long timestamp);

    @Query(value = "SELECT DISTINCT \"timestamp\"  from hdr_recommendation where COALESCE(\"timestamp\" >= :timestamp, TRUE)" +
            " order by \"timestamp\" desc", nativeQuery = true)
    List<Long> listRecentRecommendations(Long timestamp);

    @Modifying
    @Transactional
    @Query(value = "DELETE from hdr_recommendation WHERE hdr_recommendation.timestamp = :timestamp ", nativeQuery = true)
    void deleteByTimestamp(Long timestamp);

    @Query("SELECT hdr FROM HDRRecommendation hdr WHERE hdr.timestamp = :timestamp ")
    List<HDRRecommendation> findByTimestamp(Long timestamp);

    @Query("SELECT hdrr FROM HDRRecommendation hdrr WHERE hdrr.timestamp = :timestamp and hdrr.tag.id = :tagId ")
    Optional<HDRRecommendation> findByTimestampTag(Long timestamp, Long tagId);

    @Query("SELECT hdrm FROM HDRMeasurement hdrm WHERE hdrm.timestamp = :timestamp   ")
    List<HDRMeasurement> listMeasurement(Long timestamp);


}


//    @Query(
//            value = "SELECT m.*   FROM ( measurement m" +
//                    " JOIN hdr_measurement hdrm ON hdrm.measurement_id = m.id  " +
//                    " JOIN measurement_tags mt ON mt.measurement_id = m.id" +
//                    " JOIN tags   ON tags.id = mt.tag_id  )" +
//                    " WHERE hdrm.timestamp = :timestamp and" +
//                    " tags.key =  :tagKey   AND COALESCE(tags.value = CAST(:tagValue AS text)  ,TRUE) ",
//            nativeQuery = true
//    )
//    List<Measurement> listMeasurement(@Param("timestamp") LocalDateTime timestamp, @Param("tagKey") String tagKey, @Param("tagValue") String tagValue);
