package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.*;
import com.renergetic.common.utilities.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class MeasurementTileDAORequest {
    @JsonProperty(required = false)
    private Long id;
    @JsonProperty(required = false)
    private SimpleAssetDAO asset;
    @JsonProperty(required = true)
    private String name;

    @JsonProperty(value = "sensor_name", required = true)
    private String sensorName;


    //TODO: consider to remove this field
    @JsonProperty(required = false)
    private String icon;

    @JsonProperty(required = false)
    private MeasurementTypeDAORequest type;


    @JsonProperty(required = false)
    private Domain domain;

    @JsonProperty(required = false)
    private Direction direction;

    @JsonProperty
    private Map<String, ?> props;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value = "aggregation_function", required = false)
    private String function;

    public static MeasurementTileDAORequest create(Measurement measurement) {
        return MeasurementTileDAORequest.create(measurement, null, null);
    }

    public static MeasurementTileDAORequest create(Measurement measurement, String function, Map<String, ?> props) {
        MeasurementTileDAORequest dao = null;
        if (measurement != null) {
            dao = new MeasurementTileDAORequest();

            dao.setId(measurement.getId());
            dao.setName(measurement.getName());
            dao.setSensorName(measurement.getSensorName());
            if (measurement.getType() != null)
                dao.setType(MeasurementTypeDAORequest.create(measurement.getType()));
            if (measurement.getAsset() != null)
                dao.setAsset(SimpleAssetDAO.create(measurement.getAsset()));

            //dao.setDescription(measurement.getDescription());
            //dao.setIcon(measurement.getIcon());
            dao.setDomain(measurement.getDomain());
            dao.setDirection(measurement.getDirection());
            dao.setProps(props);
            dao.setFunction(function);
        }
        return dao;
    }

    public InformationTileMeasurement mapToEntity() {
        InformationTileMeasurement entity = new InformationTileMeasurement();

        if (id != null) {
            var measurement = new Measurement();
            measurement.setId(id);
//			 measurement.setDomain(domain);
//			 measurement.setDirection(direction);
//			 measurement.setSensorName(sensorName);
            entity.setMeasurement(measurement);
        } else {
            if (type != null) {
                if (type.getId() != null) {
                    MeasurementType entityType = new MeasurementType();
                    entityType.setId(type.getId());
                    entity.setType(entityType);
                } else {
                    entity.setPhysicalName(type.getPhysicalName());
                }
            }
            if (asset != null) {
                entity.setAsset(this.asset.mapToEntity());
            }
            entity.setDirection(direction);
            entity.setDomain(domain);
            entity.setSensorName(sensorName);
            entity.setMeasurementName(name);
            try {
                entity.setProps(Json.toJson(entity.getProps()));
            } catch (NullPointerException e) {
                //tODO: verify catch
                entity.setProps(Json.toJson(new HashMap<>()));
            }


        }
        if (function != null && InfluxFunction.obtain(function) != null) {
            entity.setFunction(function);
        }

        //measurement.setDescription(description);
        //measurement.setIcon(icon);


        return entity;
    }
}
