package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.common.model.NotificationDefinition;
import com.renergetic.common.model.NotificationType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class NotificationDefinitionDAO {
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;

	@JsonProperty(required = true)
	private String code;

	@JsonProperty(required = true)
	private NotificationType type;

	@JsonProperty(required = false)
	private String message;
	
	public static NotificationDefinitionDAO create(NotificationDefinition notification) {
		NotificationDefinitionDAO dao = null;
		
		if (notification != null) {
			dao = new NotificationDefinitionDAO();
			
			dao.setId(notification.getId());
			dao.setCode(notification.getCode());
			
			dao.setType(notification.getType());
			dao.setMessage(notification.getMessage());
		}
		
		return dao;
	}


	public NotificationDefinition mapToEntity() {
		NotificationDefinition notification = new NotificationDefinition();
		
		notification.setId(id);
		notification.setCode(code);

		notification.setType(type);
		notification.setMessage(message);

		return notification;
	}
}
