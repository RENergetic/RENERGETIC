package com.renergetic.hdrapi.config;

import com.renergetic.common.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unchecked")
public interface MeasurementRepository2 extends JpaRepository<Measurement, Long> {

    @Query(
            value = "SELECT m.*   FROM ( measurement m" +
                    " JOIN hdr_measurement hdrm ON hdrm.measurement_id = m.id  " +
                    " JOIN measurement_tags mt ON mt.measurement_id = m.id" +
                    " JOIN tags   ON tags.id = mt.tag_id  )" +
                    " WHERE extract(epoch from   hdrm.timestamp ) * 1000 = :timestamp and " +

                    " tags.key =  :tagKey   AND COALESCE(tags.value = CAST(:tagValue AS text)  ,TRUE) ",
            nativeQuery = true
    )
    List<Measurement> listHDRMeasurement(@Param("timestamp") Long timestamp, @Param("tagKey") String tagKey,
                                         @Param("tagValue") String tagValue);

}
