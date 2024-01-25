package com.renergetic.hdrapi.dao.temp;

import com.renergetic.common.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@SuppressWarnings("unchecked")
public interface MeasurementRepositoryTemp extends JpaRepository<Measurement, Long> {

//TODO: remove after common update
    @Query(value = "SELECT distinct measurement.* " +
            "FROM (measurement " +
            " LEFT JOIN asset asset_conn ON  measurement.asset_id = asset_conn.id " +
            "INNER JOIN measurement_type ON measurement_type.id = measurement.measurement_type_id " +
            ")" +
            " WHERE COALESCE(measurement.sensor_name = CAST(:sensorName AS text),TRUE) " +
            " AND COALESCE(measurement.domain = CAST(:domain AS text) ,TRUE) " +
            " AND COALESCE(measurement.name = CAST(:measurementName AS text),TRUE) " +
            " AND COALESCE(measurement.asset_id  = CAST ( CAST(:assetId AS text ) as bigint ) ,TRUE) " +
            " AND COALESCE(measurement.direction = CAST(:direction AS text)  ,TRUE) " +
            " AND COALESCE(measurement_type.physical_name = CAST(:physicalName AS text) ,TRUE) " +
            " AND COALESCE(measurement.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE) " +
            " LIMIT 50 " , nativeQuery = true)
    //some fields aren't optional because there would be no sense to mix them -> can be discussed
    public List<Measurement> inferMeasurement(
            Long assetId, String measurementName, String sensorName, String domain, String direction, Long type,String physicalName);


}
