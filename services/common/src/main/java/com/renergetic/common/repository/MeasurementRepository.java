package com.renergetic.common.repository;

import com.renergetic.common.dao.MeasurementDAO;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.MeasurementTags;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    List<Measurement> findByAssetIsNullAndAssetCategoryIsNull();

    List<Measurement> findByName(String name);

    Measurement save(Measurement measurement);

    List<Measurement> findByAsset(Asset assetId);

    @Query(value = "SELECT m FROM Measurement m WHERE m.id in :ids")
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

    //            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
    @Query(value = "SELECT distinct measurement.* " +
            "FROM (measurement " +
            " INNER JOIN asset asset_conn ON  measurement.asset_id = asset_conn.id " +
            " INNER JOIN measurement_type ON measurement_type.id = measurement.measurement_type_id " +
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

    //            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
    @Query(value = "SELECT distinct measurement.* " +
            "FROM (measurement " +
            "INNER INNER JOIN asset asset_conn ON  measurement.asset_id = asset_conn.id " +
            "INNER JOIN measurement_type ON measurement_type.id = measurement.measurement_type_id " +
            "LEFT JOIN measurement_details ON measurement_details.measurement_id = measurement.id " +
            ")" +
            " WHERE  measurement.asset_id = :assetId " +
            " AND  measurement_details.key =:mKey AND measurement_details.value =:mValue  ", nativeQuery = true)
    public List<Measurement> findByAssetIdAndDetails(Long assetId, @Param("mKey") String key,
                                                     @Param("mValue") String value);

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
    public List<Measurement> getByProperty(@Param("key") String key, @Param("value") String value, Long offset,
                                           Long limit);

    @Modifying
    @Transactional
    @Query(value = " INSERT INTO measurement_details ( measurement_id, key, value) VALUES " +
            " (:measurement_id, :key, :value )  ON CONFLICT (measurement_id,key) " +
            "   DO UPDATE SET value =  :value ; ", nativeQuery = true)
    public int setProperty(@Param("measurement_id") Long measurementId, @Param("key") String key,
                           @Param("value") String value);

    @Query(value = "SELECT " +
            " me.id, me.direction, me.domain, me.label,me.description," +
            " me.name, me.sensor_name as sensorName,me.sensor_id as sensorId," +
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

    @Query(
            value = "SELECT _m.* FROM  measurement _m " +
                    " WHERE  (COALESCE(_m.name like CONCAT('%', :name, '%'),:name is null ) " +
                    " or COALESCE(_m.label like CONCAT('%', :label,'%'),TRUE )) order by _m.id asc" +
                    " LIMIT :limit OFFSET :offset ;",
            nativeQuery = true
    )
    List<Measurement> filterMeasurement(@Param("name") String name, @Param("label") String label,
                                        @Param("offset") long offset, @Param("limit") int limit);

    //    @Query(value = "SELECT m.* " +
//            " FROM (measurement m " +
//            " JOIN measurement_tags mt ON mt.measurement_id = m.id   " +
//            " JOIN tags   ON tags.id = mt.tag_id  )" +
//            " WHERE tags.key =  :tagKey  " +
//            " AND COALESCE(tags.value = CAST(:tagValue AS text)  ,TRUE) " +
//            " LIMIT :limit OFFSET :offset  ", nativeQuery = true)
    @Query(value = "SELECT distinct measurement.* " +
            "FROM (measurement " +
            " LEFT JOIN asset asset_conn ON  measurement.asset_id = asset_conn.id " +
            "INNER JOIN measurement_type ON measurement_type.id = measurement.measurement_type_id " +
            " LEFT JOIN measurement_tags mt ON mt.measurement_id = measurement.id " +
            " LEFT JOIN tags   ON tags.id = mt.tag_id " +
            " LEFT JOIN measurement_tags mtPanel ON mtPanel.measurement_id = measurement.id " +
            " LEFT JOIN tags tagsPanel  ON tagsPanel.id = mtPanel.tag_id " +
            ")" +
            " WHERE COALESCE(measurement.sensor_name = CAST(:sensorName AS text),TRUE) " +
            " AND COALESCE(measurement.domain = CAST(:domain AS text) ,TRUE) " +
            " AND COALESCE(measurement.name = CAST(:measurementName AS text),TRUE) " +
            " AND COALESCE(measurement.asset_id  = CAST ( CAST(:assetId AS text ) as bigint ) ,TRUE) " +
            " AND COALESCE(measurement.direction = CAST(:direction AS text)  ,TRUE) " +
            " AND COALESCE(measurement_type.physical_name = CAST(:physicalName AS text) ,TRUE) " +

            " AND    ( CAST(:tagKey  AS text) is null or  tags.key = CAST(:tagKey  AS text)   ) " +
            " AND    ( CAST(:tagValue  AS text) is null or  tags.value = CAST(:tagValue  AS text)   ) " +
            " AND    ( CAST(:tagPanelKey  AS text) is null or  tagsPanel.key = CAST(:tagPanelKey  AS text)   ) " +
            " AND    ( CAST(:tagPanelValue  AS text) is null or  tagsPanel.value = CAST(:tagPanelValue  AS text)   ) " +
//            " AND COALESCE(tags.key = CAST(:tagKey AS text)  ,TRUE) "+
//            " AND COALESCE(tags.value = CAST(:tagValue AS text)  ,TRUE) "+
//            " AND COALESCE(tagsPanel.key = CAST(:tagPanelKey AS text)  ,TRUE) "+
//            " AND COALESCE(tagsPanel.value = CAST(:tagPanelValue AS text)  ,TRUE) "+
            " AND COALESCE(measurement.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE) " +
            " LIMIT 50 ", nativeQuery = true)
    //some fields aren't optional because there would be no sense to mix them -> can be discussed
    public List<Measurement> inferMeasurement(
            Long assetId, String measurementName, String sensorName, String domain, String direction, Long type,
            String physicalName, String tagKey, String tagValue, String tagPanelKey, String tagPanelValue);

    @Query(value = "SELECT " +
            " me.id, me.direction, me.domain, me.label,me.description, me.name, me.sensor_name as sensorName,me.sensor_id as sensorId," +
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
            "  COALESCE(me.sensor_name like ('%'||  CAST(:sensorName AS text ) ||'%') ,TRUE) " +
            " AND COALESCE(me.domain = CAST(:domain AS text) ,TRUE) " +
            " AND COALESCE(me.name like ('%'||  CAST(:measurementName AS text ) ||'%') ,TRUE) " +
            " AND (:assetId  is null or me.asset_id  = CAST ( CAST(:assetId AS text ) as bigint ))" +
            " AND COALESCE(asset.name like ('%'||  CAST(:assetName AS text ) ||'%') ,TRUE) " +
            " AND COALESCE(me.direction = CAST(:direction AS text)  ,TRUE) " +
            " AND COALESCE(mt.physical_name = CAST(:physicalName AS text) ,TRUE) " +
            " AND COALESCE(me.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE)" +
            " GROUP by me.id, mt.id,asset.id  " +
            " order by  me.asset_id,me.name, me.sensor_name, me.measurement_type_id, me.direction, me.domain, me.id asc " +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<MeasurementDAO> findMeasurements(Long assetId, String assetName, String measurementName,
                                                 String sensorName, String domain, String direction, Long type,
                                                 String physicalName, long offset, Integer limit);

//    @Query(value = "SELECT " +
//            " me.id, me.direction, me.domain, me.label,me.description, me.name, me.sensor_name as sensorName,me.sensor_id as sensorId," +
//            " me.measurement_type_id as typeId," +
//            " mt.name as typeName, mt.label as typeLabel,mt.unit, mt.physical_name as physicalName," +
//            " me.asset_id as assetId, asset.name as assetName, asset.label as assetLabel," +
//            " sum(CASE WHEN it.id is not null THEN 1 ELSE 0 END)  as panelCount " +
//            " FROM public.measurement me " +
//            " JOIN measurement_type mt on mt.id = me.measurement_type_id " +
//            " LEFT JOIN asset on asset.id= me.asset_id " +
//            " LEFT JOIN information_tile_measurement itm on itm.measurement_id = me.id " +
//            " LEFT JOIN information_tile it on it.id = itm.information_tile_id " +
//            " JOIN measurement_tags mtag on mtag.measurement_id = me.id  " +
//            " JOIN tags on tags.id = mtag.tag_id and tags.key = :tagKey  " +
//            " WHERE " +
//            "  COALESCE(me.sensor_name like ('%'||  CAST(:sensorName AS text ) ||'%') ,TRUE) " +
//            " AND COALESCE(me.domain = CAST(:domain AS text) ,TRUE) " +
//            " AND COALESCE(me.name like ('%'||  CAST(:measurementName AS text ) ||'%') ,TRUE) " +
//            " AND COALESCE(me.asset_id  = CAST ( CAST(:assetId AS text ) as bigint ) ,TRUE) " +
//            " AND COALESCE(asset.name like ('%'||  CAST(:assetName AS text ) ||'%') ,TRUE) " +
//            " AND COALESCE(me.direction = CAST(:direction AS text)  ,TRUE) " +
//            " AND COALESCE(mt.physical_name = CAST(:physicalName AS text) ,TRUE) " +
//            " AND COALESCE(me.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE)" +
//            " GROUP by me.id, mt.id,asset.id  " +
//            " order by  me.asset_id,me.name, me.sensor_name, me.measurement_type_id, me.direction, me.domain, me.id asc " +
//            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
//    public List<MeasurementDAO> findMeasurements(Long assetId, String assetName, String measurementName,
//                                                 String sensorName, String domain, String direction, Long type,
//                                                 String physicalName, String tagKey, long offset, Integer limit);

    @Query(value = "SELECT m.* " +
            " FROM (measurement m " +
            " JOIN measurement_tags mt ON mt.measurement_id = m.id  )" +
            " WHERE mt.tag_id = :tagId  LIMIT :limit OFFSET :offset  ", nativeQuery = true)
    public List<Measurement> getMeasurementByTagId(@Param("tagId") Long tagId, Long offset, Long limit);

    @Query(value = "SELECT m.* " +
            " FROM (measurement m " +
            " JOIN measurement_tags mt ON mt.measurement_id = m.id   " +
            " JOIN tags   ON tags.id = mt.tag_id  )" +
            " WHERE tags.key =  :tagKey  " +
            " AND COALESCE(tags.value = CAST(:tagValue AS text)  ,TRUE) " +
            " LIMIT :limit OFFSET :offset  ", nativeQuery = true)
    public List<Measurement> getMeasurementByTag(@Param("tagKey") String tagKey, @Param("tagValue") String tagValue,
                                                 Long offset, Long limit);

    @Query(value = "SELECT " +
            " me.id, me.direction, me.domain, me.label,me.description, me.name, me.sensor_name as sensorName,me.sensor_id as sensorId," +
            " me.measurement_type_id as typeId," +
            " mt.name as typeName, mt.label as typeLabel,mt.unit, mt.physical_name as physicalName," +
            " me.asset_id as assetId, asset.name as assetName, asset.label as assetLabel," +
            " sum(CASE WHEN it.id is not null THEN 1 ELSE 0 END)  as panelCount " +
            " FROM public.measurement me " +
            " JOIN measurement_type mt on mt.id = me.measurement_type_id " +
            " LEFT JOIN asset on asset.id= me.asset_id " +
            " LEFT JOIN information_tile_measurement itm on itm.measurement_id = me.id " +
            " LEFT JOIN information_tile it on it.id = itm.information_tile_id " +
            " JOIN measurement_tags mtag on mtag.measurement_id = me.id  " +
            " JOIN tags on tags.id = mtag.tag_id and tags.key = :tagKey  " +
            " WHERE " +
            "  COALESCE(me.sensor_name like ('%'||  CAST(:sensorName AS text ) ||'%') ,TRUE) " +
            " AND COALESCE(me.domain = CAST(:domain AS text) ,TRUE) " +
            " AND COALESCE(me.name like ('%'||  CAST(:measurementName AS text ) ||'%') ,TRUE) " +
            " AND (:assetId  is null or me.asset_id  = CAST ( CAST(:assetId AS text ) as bigint )) " +
            " AND COALESCE(asset.name like ('%'||  CAST(:assetName AS text ) ||'%') ,TRUE) " +
            " AND COALESCE(me.direction = CAST(:direction AS text)  ,TRUE) " +
            " AND COALESCE(mt.physical_name = CAST(:physicalName AS text) ,TRUE) " +
            " AND COALESCE(me.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE)" +
            " GROUP by me.id, mt.id,asset.id  " +
            " order by  me.asset_id,me.name, me.sensor_name, me.measurement_type_id, me.direction, me.domain, me.id asc " +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<MeasurementDAO> findMeasurements(Long assetId, String assetName, String measurementName,
                                                 String sensorName, String domain, String direction, Long type,
                                                 String physicalName, String tagKey, long offset, Integer limit);


    @Query(value = "SELECT  me.id, me.direction, me.domain, me.label,me.description, me.name, " +
            " me.sensor_name as sensorName,me.sensor_id as sensorId, me.measurement_type_id as typeId, " +
            " mt.name as typeName, mt.label as typeLabel,mt.unit,mt.factor,mt.base_unit, mt.physical_name as physicalName," +
            " me.asset_id as assetId, asset.name as assetName, asset.label as assetLabel," +
            " sum(CASE WHEN it.id is not null THEN 1 ELSE 0 END)  as panelCount" +
            " FROM public.measurement me" +
            " JOIN measurement_type mt on mt.id = me.measurement_type_id LEFT JOIN asset on asset.id= me.asset_id" +
            " LEFT JOIN information_tile_measurement itm on itm.measurement_id = me.id" +
            " LEFT JOIN information_tile it on it.id = itm.information_tile_id" +
            " WHERE  me.id = :id GROUP  by me.id , mt.id,asset.id ", nativeQuery = true
    )
    Optional<MeasurementDAO> findMeasurement(Long id);


    @Query(
            value = "SELECT m.*   FROM ( measurement m" +
                    " JOIN hdr_measurement hdrm ON hdrm.measurement_id = m.id  " +
                    " JOIN measurement_tags mt ON mt.measurement_id = m.id" +
                    " JOIN tags   ON tags.id = mt.tag_id  )" +
                    " WHERE hdrm.timestamp = :timestamp and" +
                    " tags.key =  :tagKey   AND COALESCE(tags.value = CAST(:tagValue AS text)  ,TRUE) ",
            nativeQuery = true
    )
    List<Measurement> listHDRMeasurement(@Param("timestamp") LocalDateTime timestamp, @Param("tagKey") String tagKey,
                                         @Param("tagValue") String tagValue);

    @Query(
            value = "SELECT m.*   FROM ( measurement m" +
                    " JOIN hdr_measurement hdrm ON hdrm.measurement_id = m.id  " +
                    " JOIN measurement_tags mt ON mt.measurement_id = m.id" +
                    " JOIN tags   ON tags.id = mt.tag_id  )" +
                    " WHERE hdrm.timestamp  = :timestamp and " +
                    " tags.key =  :tagKey   AND COALESCE(tags.value = CAST(:tagValue AS text)  ,TRUE) ",
            nativeQuery = true
    )
    List<Measurement> listHDRMeasurement(@Param("timestamp") Long timestamp, @Param("tagKey") String tagKey,
                                         @Param("tagValue") String tagValue);

    @Query(value = "SELECT " +
            " me.id, me.direction, me.domain, me.label,me.description, me.name, me.sensor_name as sensorName,me.sensor_id as sensorId," +
            " me.measurement_type_id as typeId," +
            " mt.name as typeName, mt.label as typeLabel,mt.unit, mt.physical_name as physicalName," +
            " me.asset_id as assetId, asset.name as assetName, asset.label as assetLabel," +
            " sum(CASE WHEN it.id is not null THEN 1 ELSE 0 END)  as panelCount " +
            " FROM public.measurement me " +
            " JOIN measurement_type mt on mt.id = me.measurement_type_id " +
            " LEFT JOIN asset on asset.id= me.asset_id " +
            " LEFT JOIN information_tile_measurement itm on itm.measurement_id = me.id " +
            " LEFT JOIN information_tile it on it.id = itm.information_tile_id " +
            " JOIN measurement_tags mtag on mtag.measurement_id = me.id  " +
            " JOIN tags on tags.id = mtag.tag_id and tags.key = :tagKey  " +
            " WHERE " +
            "  COALESCE(me.sensor_name like ('%'||  CAST(:sensorName AS text ) ||'%') ,TRUE) " +
            " AND COALESCE(me.domain = CAST(:domain AS text) ,TRUE) " +
            " AND COALESCE(me.name like ('%'||  CAST(:measurementName AS text ) ||'%') ,TRUE) " +
            " AND (:assetId  is null or me.asset_id  = :assetId )" +
            " AND COALESCE(asset.name like ('%'||  CAST(:assetName AS text ) ||'%') ,TRUE) " +
            " AND COALESCE(me.direction = CAST(:direction AS text)  ,TRUE) " +
            " AND COALESCE(mt.physical_name = CAST(:physicalName AS text) ,TRUE) " +
            " AND COALESCE(me.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE)" +
            " AND ( :tagValue is null OR tags.value = CAST(:tagValue AS text )  )" +
            " GROUP by me.id, mt.id,asset.id  " +
            " order by  me.asset_id,me.name, me.sensor_name, me.measurement_type_id, me.direction, me.domain, me.id asc " +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<MeasurementDAO> findMeasurementsByTag(Long assetId, String assetName, String measurementName,
                                                      String sensorName, String domain, String direction, Long type,
                                                      String physicalName, String tagKey, String tagValue, long offset,
                                                      Integer limit);

    @Query(value = "SELECT distinct " +
            " me.id, me.direction, me.domain, me.label,me.description, me.name, me.sensor_name as sensorName,me.sensor_id as sensorId," +
            " me.measurement_type_id as typeId," +
            " mt.name as typeName, mt.label as typeLabel,mt.unit, mt.physical_name as physicalName," +
            " me.asset_id as assetId, asset.name as assetName, asset.label as assetLabel," +
            " sum(CASE WHEN it.id is not null THEN 1 ELSE 0 END)  as panelCount " +
            " FROM public.measurement me " +
            " JOIN measurement_type mt on mt.id = me.measurement_type_id " +
            " LEFT JOIN asset on asset.id= me.asset_id " +
            " LEFT JOIN information_tile_measurement itm on itm.measurement_id = me.id " +
            " LEFT JOIN information_tile it on it.id = itm.information_tile_id " +
            "  LEFT JOIN measurement_tags mt1 on mt1.measurement_id = me.id " +
            " LEFT JOIN tags tt1 on tt1.id  = mt1.tag_id and tt1.key = :tagKey  " +
            " LEFT JOIN measurement_tags mt2 on mt2.measurement_id = me.id " +
            " LEFT JOIN tags tt2 on tt2.id  = mt2.tag_id and tt2.value = 'no_tag'  " +
            " WHERE (mt1.tag_id is null or  tt2.value = 'no_tag' ) " +
            " AND COALESCE(me.sensor_name like ('%'||  CAST(:sensorName AS text ) ||'%') ,TRUE) " +
            " AND COALESCE(me.domain = CAST(:domain AS text) ,TRUE) " +
            " AND COALESCE(me.name like ('%'||  CAST(:measurementName AS text ) ||'%') ,TRUE) " +
            " AND (:assetId  is null or me.asset_id  = CAST ( CAST(:assetId AS text ) as bigint )) " +
            " AND COALESCE(me.direction = CAST(:direction AS text)  ,TRUE) " +
            " AND COALESCE(mt.physical_name = CAST(:physicalName AS text) ,TRUE) " +
            " AND COALESCE(me.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE)" +
            " GROUP by me.id, mt.id,asset.id  " +
            " order by  me.asset_id,me.name, me.sensor_name, me.measurement_type_id, me.direction, me.domain, me.id asc " +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<MeasurementDAO> findMeasurementNoTag(Long assetId, String measurementName,
                                                     String sensorName, String domain, String direction, Long type,
                                                     String physicalName, String tagKey, long offset, Integer limit);

}
