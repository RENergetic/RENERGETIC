package com.renergetic.backdb.repository;

import com.renergetic.backdb.model.DemandRequest;
import com.renergetic.backdb.model.DemandSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DemandScheduleRepository extends JpaRepository<DemandSchedule, Long> {
    @Query(value = "SELECT user_demand_schedule.* " +
            "FROM (asset_connection" +
            "INNER JOIN asset asset_user ON asset_usr.id = asset_connection.asset_id" +
            "INNER JOIN asset asset_conn ON asset_conn.id = asset_connection.connected_asset_id" +
            "INNER JOIN user_demand_schedule ON user_demand_schedule.asset_id = asset_conn.asset_id)" +
            "WHERE asset_user.user_id = :userId" +
            "AND user_demand_schedule.demand_start <= :currentLocalDateTime" +
            "AND user_demand_schedule.demand_stop >= :currentLocalDateTime" +
            "LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<DemandSchedule> findByUserId(Long userId, LocalDateTime currentLocalDateTime, long offset, int limit);
    public Optional<DemandSchedule> findByAssetIdAndDemandStartLessThanEqualAndStopGreaterThanEqual(Long id, LocalDateTime dateTime, LocalDateTime dateTime2);
}