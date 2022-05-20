package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Direction;
import com.renergetic.backdb.model.Domain;
import com.renergetic.backdb.model.InformationTileMeasurement;
import com.renergetic.backdb.model.Measurement;
import com.renergetic.backdb.model.MeasurementType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InformationTileMeasurementDAORequest {
    @JsonProperty(access = Access.READ_ONLY, required = false)
    private Long id;

    @JsonProperty(required = true)
    private String props;

    @JsonProperty(value = "asset_id", required = false)
    private Long assetId;

    @JsonProperty(value = "measurement_id", required = false)
    private Long measurementId;

	@JsonProperty(value = "measurement_type_id", required = false)
	private Long typeId;

	@JsonProperty(value = "sensor_name", required = true)
	private String sensorName;

	@JsonProperty(required = false)
	private Domain domain;

	@JsonProperty(required = false)
	private Direction direction;
    
    public static InformationTileMeasurementDAORequest create(InformationTileMeasurement tile) {
    	InformationTileMeasurementDAORequest dao = new InformationTileMeasurementDAORequest();
		
		dao.setId(tile.getId());
		dao.setProps(tile.getProps());

		if(tile.getMeasurement() != null) dao.setMeasurementId(tile.getMeasurement().getId());
		if(tile.getAsset() != null) dao.setAssetId(tile.getAsset().getId());
		if(tile.getType() != null) dao.setTypeId(tile.getType().getId());

		dao.setSensorName(tile.getSensorName());
		dao.setDomain(tile.getDomain());
		dao.setDirection(tile.getDirection());
		
		return dao;
	}
	
	public InformationTileMeasurement mapToEntity() {
		InformationTileMeasurement tile = new InformationTileMeasurement();
		
		tile.setId(id);
		tile.setProps(props);
		
		if (assetId != null) {
			Asset asset = new Asset();
			asset.setId(assetId);
			tile.setAsset(asset);
		}
		if (measurementId != null) {
			Measurement measurement = new Measurement();
			measurement.setId(measurementId);
			tile.setMeasurement(measurement);
		}
		if (typeId != null) {
			MeasurementType type = new MeasurementType();
			type.setId(typeId);
			tile.setType(type);
		}

		tile.setSensorName(sensorName);
		tile.setDomain(domain);
		tile.setDirection(direction);
		return tile;
	}
}
