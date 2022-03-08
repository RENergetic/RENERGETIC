package com.renergetic.backdb.dao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Notification;
import com.renergetic.backdb.model.NotificationType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class NotificationDAO {
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;

	@JsonProperty(required = true)
	private NotificationType type;

	@JsonProperty(required = true)
	private String content;

	@JsonProperty(required = false)
	private String value;

	@JsonProperty(required = true)
	private LocalDateTime timestamp;

	@JsonProperty(required = false)
	private Long asset_id;
	
	public static NotificationDAO create(Notification notification) {
		NotificationDAO dao = null;
		
		if (notification != null) {
			dao = new NotificationDAO();
			
			dao.setId(notification.getId());
			dao.setType(notification.getType());
			dao.setContent(notification.getContent());
			dao.setValue(notification.getValue());
			dao.setTimestamp(notification.getTimestamp());
			dao.setAsset_id(notification.getAsset().getId());
		}
		
		return dao;
	}
	
	public Notification mapToEntity() {
		Notification notification = new Notification();
		
		notification.setId(id);
		notification.setType(type);
		notification.setContent(content);
		notification.setValue(value);
		notification.setTimestamp(timestamp);
		notification.setAsset(asset_id);

		return notification;
	}
}
