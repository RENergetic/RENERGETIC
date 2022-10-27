package com.renergetic.hdrapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.renergetic.hdrapi.model.*;

import java.util.List;

@SuppressWarnings("unchecked")
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
	List<Measurement> findByName (String name);
	
	Measurement save(Measurement measurement);
	List<Measurement> findByAsset(Asset assetId);

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
	public List<Measurement> findByUserIdAndAssetIdAndBySensorNameAndDomainAndDirectionAndType(Long userId,Long assetId,String measurementName, String sensorName, String domain, String direction, Long type);

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
	public List<Measurement> findByUserIdAndBySensorNameAndDomainAndDirectionAndTypeLimit(Long userId, String sensorName, Domain domain, Direction direction, MeasurementType type, Long limit, Long offset);
}
