package com.renergetic.backdb.repository;

import com.renergetic.backdb.model.DemandRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DemandRequestRepository extends JpaRepository<DemandRequest, Long> {
    @Query(value = "SELECT demand_request.* " +
            "FROM ((asset " +
            "INNER JOIN user_roles ON asset.UUID = user_roles.UUID) " +
            "INNER JOIN demand_request ON asset.id = demand_request.asset_id) " +
            "WHERE user_roles.user_id = :userId " +
            "AND demand_request.demand_request_start <= :currentLocalDateTime " +
            "AND demand_request.demand_request_stop >= :currentLocalDateTime " +
            "LIMIT :limit OFFSET :offset ;", nativeQuery = true)
    public List<DemandRequest> findByUserId(Long userId, LocalDateTime currentLocalDateTime, long offset, int limit);
    public Optional<DemandRequest> findByAssetIdAndStartLessThanEqualAndStopGreaterThanEqual(Long id, LocalDateTime dateTime, LocalDateTime dateTime2);
}