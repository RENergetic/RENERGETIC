package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.common.model.*;
import com.renergetic.common.utilities.DateConverter;
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
    @JsonProperty(required = true)
    private NotificationType type;

    @JsonProperty(required = false)
    private String message;
    //
    @JsonProperty(value = "notification_code", required = true)
    private String notificationCode;

    @JsonProperty(value = "date_from", required = true)
    private Long dateFrom;

    @JsonProperty(value = "timestamp", required = true)
    private Long timestamp;


    @JsonProperty(value = "date_to", required = false)
    private Long dateTo;

    //    @JsonProperty(value = "asset_id", required = false)
//    private Long assetId;
    @JsonProperty(value = "asset", required = false)
    private SimpleAssetDAO asset;

    @JsonProperty(value = "dashboard", required = false, access = Access.READ_ONLY)
    private DashboardDAO dashboard;

    @JsonProperty(value = "information_tile_id", required = false)
    private Long informationTileId;


    @JsonProperty(value = "measurement", required = false)
    private MeasurementDAOResponse measurement;

    @JsonProperty(value = "value", required = false)
    private Double value;

    public static NotificationScheduleDAO create(NotificationSchedule notification) {
        NotificationScheduleDAO dao = null;

        if (notification != null) {
            dao = new NotificationScheduleDAO();

            dao.setId(notification.getId());
            dao.setNotificationCode(notification.getDefinition().getCode());
            dao.setType(notification.getDefinition().getType());
            dao.setMessage(notification.getDefinition().getMessage());
            dao.setTimestamp(DateConverter.toEpoch(notification.getNotificationTimestamp()));

            dao.setDateFrom(DateConverter.toEpoch(notification.getDateFrom()));
            dao.setDateTo(DateConverter.toEpoch(notification.getDateTo()));
            if (notification.getMeasurement() != null) {
                dao.setMeasurement(MeasurementDAOResponse.create(notification.getMeasurement(), null));
            }
            dao.setValue(notification.getNotificationValue());
            if (notification.getAsset() != null)
                dao.setAsset(SimpleAssetDAO.create(notification.getAsset()));
            if (notification.getDashboard() != null)
                dao.setDashboard(DashboardDAO.create(notification.getDashboard()));
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
            definition.setType(type);
            definition.setMessage(message);
            notification.setDefinition(definition);
        }
        notification.setDateFrom(DateConverter.toLocalDateTime(dateFrom));
        notification.setDateTo(DateConverter.toLocalDateTime(dateTo));
        notification.setNotificationTimestamp(DateConverter.toLocalDateTime(timestamp));
        notification.setNotificationValue(value);
        if (asset != null) {
            notification.setAsset(asset.mapToEntity());
        }
        if (dashboard != null) {
            notification.setDashboard(dashboard.mapToEntity());
        }
        if (informationTileId != null) {
            InformationTile informationTile = new InformationTile();
            informationTile.setId(informationTileId);
            notification.setInformationTile(informationTile);
        }
        if (measurement != null) {
            notification.setMeasurement(measurement.mapToEntity());
        }
//        System.err.println(notification);
        return notification;
    }
}
