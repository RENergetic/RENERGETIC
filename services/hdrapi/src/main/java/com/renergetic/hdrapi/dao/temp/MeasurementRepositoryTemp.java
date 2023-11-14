package com.renergetic.hdrapi.dao.temp;

import com.renergetic.common.dao.MeasurementDAO;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.MeasurementTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@SuppressWarnings("unchecked")
public interface MeasurementRepositoryTemp extends JpaRepository<Measurement, Long> {


    @Query(value = "SELECT distinct measurement.* " +
            "FROM (measurement " +
            "INNER JOIN asset asset_conn ON  measurement.asset_id = asset_conn.id " +
            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
            "INNER JOIN measurement_type ON measurement_type.id = measurement.measurement_type_id " +
            ")" +
            " WHERE COALESCE(measurement.sensor_name = CAST(:sensorName AS text),TRUE) " +
            " AND COALESCE(measurement.domain = CAST(:domain AS text) ,TRUE) " +
            " AND COALESCE(measurement.name = CAST(:measurementName AS text),TRUE) " +
            " AND COALESCE(measurement.asset_id  = CAST ( CAST(:assetId AS text ) as bigint ) ,TRUE) " +
            " AND COALESCE(measurement.direction = CAST(:direction AS text)  ,TRUE) " +
            " AND COALESCE(measurement_type.physical_name = CAST(:physicalName AS text) ,TRUE) " +
            " AND COALESCE(measurement.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE) " +
            " LIMIT 50 ", nativeQuery = true)
    //some fields aren't optional because there would be no sense to mix them -> can be discussed
    public List<Measurement> inferMeasurement(
            Long assetId, String measurementName, String sensorName, String domain, String direction, Long type,
            String physicalName);


    @Query(value = "SELECT distinct measurement.* " +
            "FROM (measurement " +
            "INNER JOIN asset asset_conn ON  measurement.asset_id = asset_conn.id " +
            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
            "INNER JOIN measurement_type ON measurement_type.id = measurement.measurement_type_id " +
            ")" +
            " WHERE COALESCE(measurement.sensor_name = CAST(:sensorName AS text),TRUE) " +
            " AND COALESCE(measurement.domain = CAST(:domain AS text) ,TRUE) " +
            " AND COALESCE(measurement.name = CAST(:measurementName AS text),TRUE) " +
            " AND measurement.asset_id = :assetId " +
            " AND COALESCE(measurement.direction = CAST(:direction AS text)  ,TRUE) " +
            " AND COALESCE(measurement_type.physical_name = CAST(:physicalName AS text) ,TRUE) " +
            " AND COALESCE(measurement.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE) ", nativeQuery = true)
    //some fields aren't optional because there would be no sense to mix them -> can be discussed
    public List<Measurement> findByAssetIdAndBySensorNameAndDomainAndDirectionAndType(
            Long assetId, String measurementName, String sensorName, String domain, String direction, Long type,
            String physicalName);


    @Query(value = "SELECT " +
            " me.id, me.direction, me.domain, me.label, me.name, me.sensor_name as sensorName,me.sensor_id as sensorId," +
            " me.measurement_type_id as typeId," +
            " mt.name as typeName, mt.label as typeLabel,mt.unit, mt.physical_name as physicalName," +
            " me.asset_id as assetId, asset.name as assetName, asset.label as assetLabel," +
            " sum(CASE WHEN it.id is not null THEN 1 ELSE 0 END)  as panelCount " +
            " FROM public.measurement me " +
            " JOIN measurement_type mt on mt.id = me.measurement_type_id " +
            " LEFT JOIN asset on asset.id= me.asset_id " +
            " LEFT JOIN information_tile_measurement itm on itm.measurement_id = me.id " +
            " LEFT JOIN information_tile it on it.id = itm.information_tile_id " +
            " WHERE " +
            " AND COALESCE(measurement.sensor_name like ('%'||  CAST(:sensorName AS text ) ||'%') ,TRUE) " +
            " AND COALESCE(measurement.domain = CAST(:domain AS text) ,TRUE) " +
            " AND COALESCE(measurement.name like ('%'||  CAST(:measurementName AS text ) ||'%') ,TRUE) " +
            " AND COALESCE(measurement.asset_id  = CAST ( CAST(:assetId AS text ) as bigint ) ,TRUE) " +
            " AND COALESCE(asset.name like ('%'||  CAST(:assetName AS text ) ||'%') ,TRUE) " +
            " AND COALESCE(measurement.direction = CAST(:direction AS text)  ,TRUE) " +
            " AND COALESCE(measurement_type.physical_name = CAST(:physicalName AS text) ,TRUE) " +
            " AND COALESCE(measurement.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE)" +
            " GROUP by me.id, mt.id,asset.id  " +
            " order by  me.name, me.sensor_name, me.measurement_type_id, me.direction, me.domain  me.id, asc " +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<MeasurementDAO> findMeasurements(Long assetId, String assetName, String measurementName,
                                                 String sensorName, String domain, String direction, Long type,
                                                 String physicalName, long offset, Integer limit);


}
