package com.renergetic.backdb.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.renergetic.backdb.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@SuppressWarnings("unchecked")
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
	List<Measurement> findByName (String name);
	
	Measurement save(Measurement asset);
	List<Measurement> findByAsset(Asset assetId);
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
	public List<Measurement> findByUserIdAndBySensorNameAndDomainAndDirectionAndType(Long userId, String sensorName, Domain domain, Direction direction, MeasurementType type);
}
