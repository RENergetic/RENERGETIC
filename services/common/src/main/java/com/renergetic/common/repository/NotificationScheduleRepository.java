package com.renergetic.common.repository;

import com.renergetic.common.model.NotificationSchedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unchecked")
public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {
    NotificationSchedule save(NotificationSchedule notification);

    @Query("select n from NotificationSchedule n "
            + "WHERE n.dateFrom <= current_date AND "
            + "(n.dateTo IS NULL OR "
            + "n.dateTo  >= current_date)")
    List<NotificationSchedule> findNotExpired(Pageable pageable);

    List<NotificationSchedule> findByAssetId(Long asset_id);

    @Query("select n from NotificationSchedule n "
            + "WHERE n.notificationTimestamp >= :date_from and  (  cast(:date_to as date) is null or  n.notificationTimestamp < :date_to ) ")
    List<NotificationSchedule> listAll(@Param("date_from") LocalDateTime dateFrom, @Param("date_to") LocalDateTime dateTo, Pageable pageable);
}
