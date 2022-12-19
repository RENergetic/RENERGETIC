package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.Dashboard;
import com.renergetic.hdrapi.model.InformationTile;
import com.renergetic.hdrapi.model.Notification;
import com.renergetic.hdrapi.model.NotificationType;

import com.renergetic.hdrapi.service.utils.DateConverter;
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

	@JsonProperty(value = "dashboard", required = false)
	private DashboardDAO dashboard;

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

				dao.setDashboard( DashboardDAO.create(notification.getDashboard()));
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
		if (dashboard != null) {
			Dashboard mDashboard = new Dashboard();
			mDashboard.setId(dashboard.getId());
			notification.setDashboard(mDashboard);
		}
		if (informationTileId != null) {
			InformationTile informationTile = new InformationTile();
			informationTile.setId(informationTileId);
			notification.setInformationTile(informationTile);
		}
		return notification;
	}
}
