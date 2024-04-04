package com.renergetic.hdrapi.dao.tempcommon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface TempHDRMeasurementRepository extends JpaRepository<HDRMeasurement, Long> {
    @Query("SELECT hdrm FROM HDRMeasurement hdrm WHERE hdrm.timestamp = :timestamp   ")
    List<HDRMeasurement> listMeasurement(LocalDateTime timestamp);
    @Query("SELECT hdrm FROM HDRMeasurement hdrm WHERE hdrm.measurement.id = :measurementId and hdrm.timestamp = :timestamp   ")
    Optional<HDRMeasurement> findByIdAndTimestamp(Long measurementId,LocalDateTime timestamp );
}
