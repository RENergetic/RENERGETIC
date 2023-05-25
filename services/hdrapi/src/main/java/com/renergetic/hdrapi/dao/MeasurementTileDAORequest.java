package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.*;
import com.renergetic.hdrapi.service.utils.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.json.ParseException;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class MeasurementTileDAORequest {
    @JsonProperty(required = false)
    private Long id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(value = "sensor_name", required = true)
    private String sensorName;


    @JsonProperty(required = false)
    private String icon;

    @JsonProperty(required = false)
    private MeasurementType type;


    @JsonProperty(required = false)
    private Domain domain;

    @JsonProperty(required = false)
    private Direction direction;

    @JsonProperty
    private Map<String, ?> props;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value = "aggregation_function", required = false)
    private String function;

//	public static MeasurementTileDAORequest create(Measurement measurement) {
//		MeasurementTileDAORequest dao = null;
//
//		if (measurement != null) {
//			dao = new MeasurementTileDAORequest();
//
//			dao.setId(measurement.getId());
//			dao.setName(measurement.getName());
//			dao.setSensorName(measurement.getSensorName());
//			if (measurement.getType() != null)
//				dao.setType(measurement.getType().getId());
//			dao.setLabel(measurement.getLabel());
//			//dao.setDescription(measurement.getDescription());
//			//dao.setIcon(measurement.getIcon());
//			dao.setDomain(measurement.getDomain());
//			dao.setDirection(measurement.getDirection());
//			if (measurement.getAsset() != null)
//				dao.setAssetId(measurement.getAsset().getId());
//		}
//		return dao;
//	}

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
                MeasurementType entityType = new MeasurementType();
                entityType.setId(type.getId());
                entity.setType(entityType);
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
        if (function != null && InfluxFunction.obtain(function)!= null) {
            entity.setFunction(function);
        }

        //measurement.setDescription(description);
        //measurement.setIcon(icon);


        return entity;
    }
}
