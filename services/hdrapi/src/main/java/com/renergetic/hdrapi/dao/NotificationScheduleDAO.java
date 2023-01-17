package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.Dashboard;
import com.renergetic.hdrapi.model.InformationTile;
import com.renergetic.hdrapi.model.NotificationDefinition;
import com.renergetic.hdrapi.model.NotificationSchedule;
import com.renergetic.hdrapi.service.utils.DateConverter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class NotificationScheduleDAO {
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;

	@JsonProperty(value = "notification_code", required = true)
	private String notificationCode;

	@JsonProperty(value = "date_from",required = true)
	private Long dateFrom;

	@JsonProperty(value = "date_to",required = false)
	private Long dateTo;

	@JsonProperty(value = "asset_id", required = false)
	private Long assetId;

	@JsonProperty(value = "dashboard_id", required = false)
	private Long dashboardId;

	@JsonProperty(value = "information_tile_id", required = false)
	private Long informationTileId;
	
	public static NotificationScheduleDAO create(NotificationSchedule notification) {
		NotificationScheduleDAO dao = null;
		
		if (notification != null) {
			dao = new NotificationScheduleDAO();
			
			dao.setId(notification.getId());
			dao.setNotificationCode(notification.getDefinition().getCode());
			
			dao.setDateFrom(DateConverter.toEpoch(notification.getDateFrom()));
			dao.setDateTo(DateConverter.toEpoch(notification.getDateTo()));
			
			if (notification.getAsset() != null)
				dao.setAssetId(notification.getAsset().getId());
			if (notification.getDashboard() != null)
				dao.setDashboardId( notification.getDashboard().getId());
			if (notification.getInformationTile() != null)
				dao.setInformationTileId(notification.getInformationTile().getId());
		}
		
		return dao;
	}

	public NotificationSchedule mapToEntity() {
		return mapToEntity(null);
	}
	

	public NotificationSchedule mapToEntity(NotificationDefinition definition) {
		NotificationSchedule notification = new NotificationSchedule();
		
		notification.setId(id);
		
		if (definition != null)
			notification.setDefinition(definition);
		else {
			definition = new NotificationDefinition();
			definition.setCode(notificationCode);
			notification.setDefinition(definition);
		}
		notification.setDateFrom(DateConverter.toLocalDateTime(dateFrom));
		notification.setDateTo(DateConverter.toLocalDateTime(dateTo));

		if (assetId != null) {
			Asset asset = new Asset();
			asset.setId(assetId);
			notification.setAsset(asset);
		}
		if (dashboardId != null) {
			Dashboard dashboard = new Dashboard();
			dashboard.setId(dashboardId);
			notification.setDashboard(dashboard);
		}
		if (informationTileId != null) {
			InformationTile informationTile = new InformationTile();
			informationTile.setId(informationTileId);
			notification.setInformationTile(informationTile);
		}
		System.err.println(notification);
		return notification;
	}
}
