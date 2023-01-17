package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.NotificationSchedule;
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

	@JsonProperty(value = "date_from",required = true)
	private Long dateFrom;

	@JsonProperty(value = "date_to",required = false)
	private Long dateTo;

	@JsonProperty(value = "asset_id", required = false)
	private Long assetId;

	@JsonProperty(value = "dashboard", required = false, access = Access.READ_ONLY)
	private DashboardDAO dashboard;

	@JsonProperty(value = "information_tile_id", required = false)
	private Long informationTileId;
	
	public static NotificationDAO create(NotificationSchedule notification) {
		NotificationDAO dao = null;
		
		if (notification != null) {
			dao = new NotificationDAO();
			
			dao.setId(notification.getId());
			dao.setType(notification.getDefinition().getType());
			dao.setMessage(notification.getDefinition().getMessage());

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

}
