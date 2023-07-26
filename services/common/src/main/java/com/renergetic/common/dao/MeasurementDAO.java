package com.renergetic.common.dao;

import com.renergetic.common.model.Direction;
import com.renergetic.common.model.Domain;

public interface MeasurementDAO {
    Long getId();

    String getName();

    String getLabel();

    Direction getDirection();

    Domain getDomain();

    String getSensorName();

    String getPhysicalName();

    String getSensorId();

    Long getTypeId();

    String getTypeName();

    String getTypeLabel();

    String getUnit();

    Long getAssetId();

    String getAssetName();

    String getAssetLabel();

    int getPanelCount();
}
