package com.renergetic.common.repository;

import com.renergetic.common.model.HDRRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface HDRRequestRepository extends JpaRepository<HDRRequest, Long> {


    HDRRequest save(HDRRequest request);

    @Query(value = "SELECT  max(\"timestamp\") as \"timestamp\" from hdr_request", nativeQuery = true)
    Optional<Long> getRecentRequestTimestamp();

    @Query("SELECT hdrr FROM HDRRequest hdrr WHERE hdrr.timestamp = :timestamp ")
    Optional<HDRRequest> findRequestByTimestamp(Long timestamp);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM hdr_request WHERE hdr_request.timestamp = :timestamp ", nativeQuery = true)
    void deleteByTimestamp(Long timestamp);
}
