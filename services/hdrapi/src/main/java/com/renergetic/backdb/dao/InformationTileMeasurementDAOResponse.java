package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Direction;
import com.renergetic.backdb.model.Domain;
import com.renergetic.backdb.model.InformationTileMeasurement;
import com.renergetic.backdb.model.MeasurementType;

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

//    @JsonProperty(value = "asset", required = false)
//    private SimpleAssetDAO asset;

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
    
    public static InformationTileMeasurementDAOResponse create(InformationTileMeasurement tile) {
    	InformationTileMeasurementDAOResponse dao = new InformationTileMeasurementDAOResponse();
		
		dao.setId(tile.getId());
		dao.setProps(tile.getProps());

		if(tile.getMeasurement() != null) 
			dao.setMeasurement(SimpleMeasurementDAO.create(tile.getMeasurement()));
//		if(tile.getAsset() != null) 
//			dao.setAsset(SimpleAssetDAO.create(tile.getAsset()));
		if(tile.getType() != null)
			dao.setType(tile.getType());

		dao.setSensorName(tile.getSensorName());
		dao.setDomain(tile.getDomain());
		dao.setDirection(tile.getDirection());
		
		return dao;
	}
	
	public InformationTileMeasurement mapToEntity() {
		InformationTileMeasurement tile = new InformationTileMeasurement();
		
		tile.setId(id);
		tile.setProps(props);
		
//		if (asset != null) 
//			tile.setAsset(asset.mapToEntity());

		if (measurement != null)
			tile.setMeasurement(measurement.mapToEntity());
		
		if (type != null)
			tile.setType(type);

		tile.setSensorName(sensorName);
		tile.setDomain(domain);
		tile.setDirection(direction);

		return tile;
	}
}
