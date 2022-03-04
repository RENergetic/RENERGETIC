package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.Notification;

@SuppressWarnings("unchecked")
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	Notification save(Notification notification);
	
	List<Notification> findByAssetId(Long asset_id);
}
