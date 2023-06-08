//package com.renergetic.hdrapi.dao;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonProperty.Access;
//import com.renergetic.hdrapi.model.Direction;
//import com.renergetic.hdrapi.model.Domain;
//import com.renergetic.hdrapi.model.InfluxFunction;
//import com.renergetic.hdrapi.model.InformationTile;
//import com.renergetic.hdrapi.model.InformationTileMeasurement;
//import com.renergetic.hdrapi.model.Measurement;
//import com.renergetic.hdrapi.model.MeasurementType;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//@Getter
//@Setter
//@RequiredArgsConstructor
//@ToString
//public class InformationTileMeasurementDAORequest {
//    @JsonProperty(access = Access.READ_ONLY, required = false)
//    private Long id;
//
//    @JsonProperty(required = true)
//    private String props;
//
//    @JsonProperty(value = "measurement_id", required = false)
//    private Long measurementId;
//
//    @JsonProperty(value = "measurement_type_id", required = false)
//    private Long typeId;
//    @JsonProperty(value = "physical_name", required = false)
//    private String physicalName;
//
//    @JsonProperty(value = "sensor_name", required = false)
//    private String sensorName;
//
//    @JsonProperty(required = false)
//    private Domain domain;
//
//    @JsonProperty(required = false)
//    private Direction direction;
//
//    @JsonProperty(required = false)
//    private InfluxFunction function;
//
//    @JsonProperty(value = "information_tile_id", required = false)
//    private Long informationTile;
//
//    public static InformationTileMeasurementDAORequest create(InformationTileMeasurement tile) {
//        InformationTileMeasurementDAORequest dao = new InformationTileMeasurementDAORequest();
//
//        dao.setId(tile.getId());
//        dao.setProps(tile.getProps());
//
//        if (tile.getMeasurement() != null) dao.setMeasurementId(tile.getMeasurement().getId());
//        if (tile.getType() != null) dao.setTypeId(tile.getType().getId());
//
//        dao.setSensorName(tile.getSensorName());
//        dao.setDomain(tile.getDomain());
//        dao.setDirection(tile.getDirection());
//        dao.setFunction(InfluxFunction.obtain(tile.getFunction()));
//        if (tile.getInformationTile() != null) dao.setInformationTile(tile.getInformationTile().getId());
//
//        return dao;
//    }
//
//    public InformationTileMeasurement mapToEntity() {
//        InformationTileMeasurement tile = new InformationTileMeasurement();
//
//        tile.setId(id);
//        tile.setProps(props);
//        tile.setPhysicalName(physicalName);
//        if (measurementId != null) {
//            Measurement measurement = new Measurement();
//            measurement.setId(measurementId);
//            tile.setMeasurement(measurement);
//        }
//        if (typeId != null) {
//            MeasurementType type = new MeasurementType();
//            type.setId(typeId);
//            tile.setType(type);
//        }
//        if (informationTile != null) {
//            InformationTile infoTile = new InformationTile();
//            infoTile.setId(informationTile);
//            tile.setInformationTile(infoTile);
//        }
//
//        tile.setSensorName(sensorName);
//        tile.setDomain(domain);
//        tile.setDirection(direction);
//        tile.setFunction(function != null ? function.name().toLowerCase() : null);
//        return tile;
//    }
//}
