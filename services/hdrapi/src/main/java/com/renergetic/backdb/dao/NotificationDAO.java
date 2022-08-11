package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Dashboard;
import com.renergetic.backdb.model.InformationTile;
import com.renergetic.backdb.model.Notification;
import com.renergetic.backdb.model.NotificationType;

import com.renergetic.backdb.service.utils.DateConverter;
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
	
	public static NotificationDAO create(Notification notification) {
		NotificationDAO dao = null;
		
		if (notification != null) {
			dao = new NotificationDAO();
			
			dao.setId(notification.getId());
			dao.setType(notification.getType());
			dao.setMessage(notification.getMessage());
			dao.setIcon(notification.getIcon());

			dao.setDateFrom(DateConverter.toEpoch(notification.getDateFrom()));
			dao.setDateTo(DateConverter.toEpoch(notification.getDateTo()));
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
		return notification;
	}
}
