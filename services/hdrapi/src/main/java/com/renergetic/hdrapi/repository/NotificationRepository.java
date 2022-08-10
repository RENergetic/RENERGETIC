package com.renergetic.hdrapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.Notification;

@SuppressWarnings("unchecked")
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	Notification save(Notification notification);
	
	List<Notification> findByAssetId(Long asset_id);
}
