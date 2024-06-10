package com.renergetic.common.repository;

import com.renergetic.common.model.HDRMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HDRMeasurementRepository extends JpaRepository<HDRMeasurement, Long> {

    @Query("SELECT hdrm FROM HDRMeasurement hdrm WHERE hdrm.timestamp = :timestamp   ")
    List<HDRMeasurement> listMeasurement(Long timestamp);
    @Query("SELECT hdrm FROM HDRMeasurement hdrm WHERE hdrm.measurement.id = :measurementId and hdrm.timestamp = :timestamp   ")
    Optional<HDRMeasurement> findByIdAndTimestamp(Long measurementId, Long timestamp );
}
