package com.renergetic.hdrapi.dao.temp;

import com.renergetic.common.model.HDRRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface HDRRequestRepository extends JpaRepository<HDRRequest, Long> {


    HDRRequest save(HDRRequest request);

    @Query(value = "SELECT max(`timestamp`) as `timestamp` from hdr_request", nativeQuery = true)
    Optional<LocalDateTime> getRecentRequestTimestamp();

    @Query("SELECT hdrr FROM HDRRequest hdrr WHERE hdrr.timestamp = :timestamp ")
    Optional<HDRRequest> findRequestByTimestamp(LocalDateTime timestamp);

    @Query(value = "DELETE hdr_request WHERE hdr_request.timestamp = :timestamp ", nativeQuery = true)
    void deleteByTimestamp(LocalDateTime timestamp);
}
