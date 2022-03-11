package com.renergetic.backdb.dao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

	@JsonProperty(required = false)
	private String message;

	@JsonProperty(required = true)
	private LocalDateTime date_from;

	@JsonProperty(required = false)
	@JsonInclude(Include.NON_NULL)
	private LocalDateTime date_to;

	@JsonProperty(required = false)
	@JsonInclude(Include.NON_NULL)
	private Long asset_id;

	@JsonProperty(required = false)
	@JsonInclude(Include.NON_NULL)
	private Long dashboard_id;
	
	public static NotificationDAO create(Notification notification) {
		NotificationDAO dao = null;
		
		if (notification != null) {
			dao = new NotificationDAO();
			
			dao.setId(notification.getId());
			dao.setType(notification.getType());
			dao.setMessage(notification.getMessage());
			dao.setDate_from(notification.getDateFrom());
			dao.setDate_to(notification.getDateTo());
			dao.setAsset_id(notification.getAsset().getId());
			dao.setDashboard_id(notification.getDashboard().getId());
		}
		
		return dao;
	}
	
	public Notification mapToEntity() {
		Notification notification = new Notification();
		
		notification.setId(id);
		notification.setType(type);
		notification.setMessage(message);
		notification.setDateFrom(date_from);
		notification.setDateTo(date_to);
		notification.setAsset(asset_id);
		notification.setDashboard(dashboard_id);

		return notification;
	}
}
