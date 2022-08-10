package com.renergetic.hdrapi.dao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.Dashboard;
import com.renergetic.hdrapi.model.InformationTile;
import com.renergetic.hdrapi.model.Notification;
import com.renergetic.hdrapi.model.NotificationType;

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

	@JsonProperty(required = false)
	private String icon;

	@JsonProperty(required = true)
	private LocalDateTime date_from;

	@JsonProperty(required = false)
	private LocalDateTime date_to;

	@JsonProperty(value = "asset_id", required = false)
	private Long assetId;

	@JsonProperty(value = "dashboard_id", required = false)
	private Long dashboardId;

	@JsonProperty(value = "information_tile_id", required = false)
	private Long informationTileId;
	
	public static NotificationDAO create(Notification notification) {
		NotificationDAO dao = null;
		
		if (notification != null) {
			dao = new NotificationDAO();
			
			dao.setId(notification.getId());
			dao.setType(notification.getType());
			dao.setMessage(notification.getMessage());
			dao.setIcon(notification.getIcon());
			dao.setDate_from(notification.getDateFrom());
			dao.setDate_to(notification.getDateTo());
			if (notification.getAsset() != null)
				dao.setAssetId(notification.getAsset().getId());
			if (notification.getDashboard() != null)
				dao.setDashboardId(notification.getDashboard().getId());
			if (notification.getInformationTile() != null)
				dao.setInformationTileId(notification.getInformationTile().getId());
		}
		
		return dao;
	}
	
	public Notification mapToEntity() {
		Notification notification = new Notification();
		
		notification.setId(id);
		notification.setType(type);
		notification.setMessage(message);
		notification.setIcon(icon);
		notification.setDateFrom(date_from);
		notification.setDateTo(date_to);
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
		return notification;
	}
}
