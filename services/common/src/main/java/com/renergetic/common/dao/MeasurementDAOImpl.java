package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.Direction;
import com.renergetic.common.model.Domain;
import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.dao.details.MeasurementTagsDAO;

import java.util.List;

import lombok.Data;

@Data
public class MeasurementDAOImpl {
    @JsonProperty(required = false)
    private Long id;

    @JsonProperty(required = false)
    private String name;

    @JsonProperty(value = "sensor_name", required = false)
    private String sensorName;

    @JsonProperty(required = false)
    private String label;

    @JsonProperty(required = false)
    private MeasurementType type;

    @JsonProperty(required = false)
    private Domain domain;

    @JsonProperty(required = false)
    private Direction direction;

    @JsonProperty(required = false)
    private SimpleAssetDAO asset;

    @JsonProperty(value = "panel_count", required = false)
    private int panelCount;
    @JsonProperty(value = "tags", required = false)
    private List<MeasurementTagsDAO> tags=null;

    public static MeasurementDAOImpl create(MeasurementDAO measurement ) {
        MeasurementDAOImpl dao = null;

        if (measurement != null) {
            dao = new MeasurementDAOImpl();
            dao.setId(measurement.getId());
            dao.setName(measurement.getName());
            dao.setSensorName(measurement.getSensorName());
            dao.setLabel(measurement.getLabel());
            dao.setDomain(measurement.getDomain());
            dao.setDirection(measurement.getDirection());
            var mt = new MeasurementType();
            mt.setId(measurement.getTypeId());
            mt.setLabel(measurement.getTypeLabel());
            mt.setName(measurement.getTypeName());
            mt.setPhysicalName(measurement.getPhysicalName());
            mt.setUnit(measurement.getUnit());
            dao.setType(mt);
            if (measurement.getAssetId() != null) {
                var asset = new SimpleAssetDAO();
                asset.setId(measurement.getAssetId());
                asset.setLabel(measurement.getAssetLabel());
                asset.setName(measurement.getAssetName());
                dao.setAsset(asset);
            }
            dao.setPanelCount(measurement.getPanelCount());

        }
        return dao;
    }

}