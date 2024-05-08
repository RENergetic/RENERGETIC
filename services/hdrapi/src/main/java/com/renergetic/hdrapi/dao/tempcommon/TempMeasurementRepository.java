//package com.renergetic.hdrapi.dao.tempcommon;
//
//import com.renergetic.common.dao.MeasurementDAO;
//import com.renergetic.common.model.*;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import java.util.List;
//
//@SuppressWarnings("unchecked")
//public interface TempMeasurementRepository extends JpaRepository<Measurement, Long> {
//
//
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
//            " AND (:assetId  is null or me.asset_id  = :assetId )" +
//            " AND COALESCE(asset.name like ('%'||  CAST(:assetName AS text ) ||'%') ,TRUE) " +
//            " AND COALESCE(me.direction = CAST(:direction AS text)  ,TRUE) " +
//            " AND COALESCE(mt.physical_name = CAST(:physicalName AS text) ,TRUE) " +
//            " AND COALESCE(me.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE)" +
//            " AND ( :tagValue is null OR tags.value = CAST(:tagValue AS text )  )" +
//            " GROUP by me.id, mt.id,asset.id  " +
//            " order by  me.asset_id,me.name, me.sensor_name, me.measurement_type_id, me.direction, me.domain, me.id asc " +
//            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
//    public List<MeasurementDAO> findMeasurementsByTag(Long assetId, String assetName, String measurementName,
//                                                      String sensorName, String domain, String direction, Long type,
//                                                      String physicalName, String tagKey,  String tagValue,  long offset, Integer limit);
//
//    @Query(value = "SELECT distinct " +
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
//            "  LEFT JOIN measurement_tags mt1 on mt1.measurement_id = me.id " +
//            " LEFT JOIN tags tt1 on tt1.id  = mt1.tag_id and tt1.key = :tagKey  " +
//            " LEFT JOIN measurement_tags mt2 on mt2.measurement_id = me.id " +
//            " LEFT JOIN tags tt2 on tt2.id  = mt2.tag_id and tt2.value = 'no_tag'  " +
//            " WHERE (mt1.tag_id is null or  tt2.value = 'no_tag' ) " +
//            " AND COALESCE(me.sensor_name like ('%'||  CAST(:sensorName AS text ) ||'%') ,TRUE) " +
//            " AND COALESCE(me.domain = CAST(:domain AS text) ,TRUE) " +
//            " AND COALESCE(me.name like ('%'||  CAST(:measurementName AS text ) ||'%') ,TRUE) " +
//            " AND (:assetId  is null or me.asset_id  = CAST ( CAST(:assetId AS text ) as bigint )) " +
//            " AND COALESCE(me.direction = CAST(:direction AS text)  ,TRUE) " +
//            " AND COALESCE(mt.physical_name = CAST(:physicalName AS text) ,TRUE) " +
//            " AND COALESCE(me.measurement_type_id  = CAST ( CAST(:type AS text ) as bigint ) ,TRUE)" +
//            " GROUP by me.id, mt.id,asset.id  " +
//            " order by  me.asset_id,me.name, me.sensor_name, me.measurement_type_id, me.direction, me.domain, me.id asc " +
//            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)
//    public List<MeasurementDAO> findMeasurementNoTag(Long assetId,   String measurementName,
//                                                 String sensorName, String domain, String direction, Long type,
//                                                 String physicalName, String tagKey, long offset, Integer limit);
//
//
//
//}
