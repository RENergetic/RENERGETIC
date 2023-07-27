package com.renergetic.kpiapi.repository;

import com.renergetic.hdrapi.dao.projection.MeasurementDAO;
import com.renergetic.hdrapi.model.details.MeasurementTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.renergetic.kpiapi.model.*;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import java.util.List;

@SuppressWarnings("unchecked")
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    List<Measurement> findByName(String name);

    Measurement save(Measurement measurement);

    List<Measurement> findByAsset(Asset assetId);
    @Query(value = "SELECT m FROM Measurement m WHERE m.id in :ids" )
    public List<Measurement> findByIds(List<Long> ids);
    @Query(value = "SELECT measurement.* " +
            "FROM (measurement " +
            "INNER JOIN asset asset_conn ON  measurement.asset_id = asset_conn.id " +
            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
            "INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId) ", nativeQuery = true)
    public List<Measurement> findByUserId(Long userId);


    @Query(value = "SELECT measurement.* " +
            "FROM (measurement " +
            "INNER JOIN asset asset_conn ON  measurement.asset_id = asset_conn.id " +
            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
            "INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId) " +
            "WHERE measurement.sensor_name = :sensorName " +
            "AND COALESCE(measurement.domain = :domain,TRUE) " +
            "AND COALESCE(measurement.name = :measurementName,TRUE) " +
            "AND measurement.asset_id = :assetId " +
            "AND COALESCE(measurement.direction = :direction,TRUE) " +
            "AND  measurement.measurement_type_id = :type  ", nativeQuery = true)
    //some fields aren't optional because there would be no sense to mix them -> can be discussed
    //TODO: not sure if user id and connection is required here - unless we want to check here if the user can view the data/strcuture of the panel
    public List<Measurement> findByUserIdAndAssetIdAndBySensorNameAndDomainAndDirectionAndType(Long userId,
                                                                                               Long assetId,
                                                                                               String measurementName,
                                                                                               String sensorName,
                                                                                               String domain,
                                                                                               String direction,
                                                                                               Long type);

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
            " AND COALESCE(measurement.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE) " , nativeQuery = true)
    //some fields aren't optional because there would be no sense to mix them -> can be discussed
    public List<Measurement> findByAssetIdAndBySensorNameAndDomainAndDirectionAndType(
            Long assetId, String measurementName, String sensorName, String domain, String direction, Long type,String physicalName);

    @Query(value = "SELECT measurement.* " +
            "FROM (asset asset_conn " +
            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
            "INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId " +
            "INNER JOIN measurement ON measurement.asset_id = asset_conn.id) " +
            "WHERE measurement.sensor_name = :sensorName " +
            "AND measurement.domain >= :domain " +
            "AND measurement.direction >= :direction " +
            "AND measurement.type >= :type " +
            "LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<Measurement> findByUserIdAndBySensorNameAndDomainAndDirectionAndTypeLimit(Long userId,
                                                                                          String sensorName,
                                                                                          Domain domain,
                                                                                          Direction direction,
                                                                                          MeasurementType type,
                                                                                          Long limit, Long offset);

    @Query(value = "SELECT measurement.* " +
            " FROM (measurement " +
            " INNER JOIN measurement_details ON " +
            " measurement_details.measurement_id = measurement.id and measurement_details.key=:key  ) " +
            " WHERE measurement_details.value = :value " +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<Measurement> getByProperty(@Param("key")  String key, @Param("value") String value, Long offset, Long limit);
    @Modifying
    @Transactional
    @Query(value = " INSERT INTO measurement_details ( measurement_id, key, value) VALUES " +
            " (:measurement_id, :key, :value )  ON CONFLICT (measurement_id,key) " +
            "   DO UPDATE SET value =  :value ; ", nativeQuery = true)
    public int setProperty(@Param("measurement_id") Long measurementId,@Param("key")  String key, @Param("value") String value );


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
            " GROUP by me.id, mt.id,asset.id  " +
            " order by me.id asc " +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<MeasurementDAO> report(long offset, long limit);


    @Query("SELECT DISTINCT informationPanel FROM InformationPanel informationPanel " +
            " JOIN informationPanel.tiles tile " +
            " JOIN tile.informationTileMeasurements tileMeasurement " +
            " JOIN tileMeasurement.measurement measurement" +
            "  WHERE measurement.id = :measurementId")
    public List<InformationPanel> getLinkedPanels(Long measurementId);
    @Query("SELECT tag FROM MeasurementTags tag " +
            " JOIN tag.measurements measurement " +
            "  WHERE measurement.id = :measurementId")
    public List<MeasurementTags> getTags(Long measurementId);
}
