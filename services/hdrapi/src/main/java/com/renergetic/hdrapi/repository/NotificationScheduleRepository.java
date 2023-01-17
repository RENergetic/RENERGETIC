package com.renergetic.hdrapi.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.renergetic.hdrapi.model.NotificationSchedule;

@SuppressWarnings("unchecked")
public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {
	NotificationSchedule save(NotificationSchedule notification);
	
	@Query("select n from NotificationSchedule n "
			+ "WHERE n.dateFrom <= current_date AND "
			+ "(n.dateTo IS NULL OR "
			+ "n.dateTo  >= current_date)")
	List<NotificationSchedule> findNotExpired(Pageable pageable);
	
	List<NotificationSchedule> findByAssetId(Long asset_id);
}
