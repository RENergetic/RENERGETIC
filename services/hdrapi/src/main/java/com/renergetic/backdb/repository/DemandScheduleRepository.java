package com.renergetic.backdb.repository;

import com.renergetic.backdb.model.DemandSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DemandScheduleRepository extends JpaRepository<DemandSchedule, Long> {
    @Query(value = "SELECT user_demand_schedule.* " +
            "FROM (asset asset_conn " +
            "INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
            "INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId " +
            "INNER JOIN user_demand_schedule ON user_demand_schedule.asset_id = asset_conn.id) " +
            "WHERE user_demand_schedule.demand_start <= :currentLocalDateTime " +
            "AND user_demand_schedule.demand_stop >= :currentLocalDateTime " +
            "AND :userId = :userId " +
            "LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<DemandSchedule> findByUserId(Long userId, LocalDateTime currentLocalDateTime, long offset, int limit);
    public Optional<DemandSchedule> findByAssetIdAndDemandStartLessThanEqualAndDemandStopGreaterThanEqual(Long id, LocalDateTime dateTime, LocalDateTime dateTime2);
}