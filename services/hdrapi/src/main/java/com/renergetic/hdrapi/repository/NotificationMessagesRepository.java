package com.renergetic.hdrapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.NotificationMessages;

@SuppressWarnings("unchecked")
public interface NotificationMessagesRepository extends JpaRepository<NotificationMessages, String> {
	
	NotificationMessages save(NotificationMessages message);
	long count();
}
