package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.dao.MeasurementDAO;
import com.renergetic.common.dao.SimpleAssetDAO;
import com.renergetic.common.model.Direction;
import com.renergetic.common.model.Domain;
import com.renergetic.common.model.MeasurementType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
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
//    @JsonProperty(value = "measurement_details", required = false)
//    private HashMap<String, ?> measurementDetails;


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
            //TODO: DAO
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


//            if (details != null && !details.isEmpty()) {
//                HashMap<String, String> detailsDao = details.stream().filter(it -> it.getValue() != null)
//                        .collect(Collectors.toMap(MeasurementDetails::getKey, MeasurementDetails::getValue,
//                                (prev, next) -> next, HashMap::new));
//
//                dao.setMeasurementDetails(detailsDao);
//            }

        }
        return dao;
    }


}