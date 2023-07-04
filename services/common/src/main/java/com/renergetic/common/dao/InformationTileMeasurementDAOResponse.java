package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.common.model.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InformationTileMeasurementDAOResponse {
    @JsonProperty(access = Access.READ_ONLY, required = false)
    private Long id;

    @JsonProperty(required = true)
    private String props;

    @JsonProperty(value = "measurement", required = false)
    private SimpleMeasurementDAO measurement;

	@JsonProperty(value = "measurement_type_id", required = false)
	private MeasurementType type;

	@JsonProperty(value = "sensor_name", required = false)
	private String sensorName;

	@JsonProperty(required = false)
	private Domain domain;

	@JsonProperty(required = false)
	private Direction direction;

	@JsonProperty(required = false)
	private InfluxFunction function;

	@JsonProperty(value = "information_tile_id", required = false)
	private Long informationTile;
    
    public static InformationTileMeasurementDAOResponse create(InformationTileMeasurement tile) {
    	InformationTileMeasurementDAOResponse dao = new InformationTileMeasurementDAOResponse();
		
		dao.setId(tile.getId());
		dao.setProps(tile.getProps());

		if(tile.getMeasurement() != null) 
			dao.setMeasurement(SimpleMeasurementDAO.create(tile.getMeasurement()));
		if(tile.getType() != null)
			dao.setType(tile.getType());

		dao.setSensorName(tile.getSensorName());
		dao.setDomain(tile.getDomain());
		dao.setDirection(tile.getDirection());
		dao.setFunction(InfluxFunction.obtain(tile.getFunction()));
		if(tile.getInformationTile() != null) dao.setInformationTile(tile.getInformationTile().getId());
		
		return dao;
	}
	
	public InformationTileMeasurement mapToEntity() {
		InformationTileMeasurement tile = new InformationTileMeasurement();
		
		tile.setId(id);
		tile.setProps(props);

		if (measurement != null)
			tile.setMeasurement(measurement.mapToEntity());
		
		if (type != null)
			tile.setType(type);

		tile.setSensorName(sensorName);
		tile.setDomain(domain);
		tile.setDirection(direction);
		tile.setFunction(function != null? function.name().toLowerCase() : null);
		
		if (informationTile != null) {
			InformationTile infoTile = new InformationTile();
			infoTile.setId(informationTile);
			tile.setInformationTile(infoTile);
		}


		return tile;
	}
}
