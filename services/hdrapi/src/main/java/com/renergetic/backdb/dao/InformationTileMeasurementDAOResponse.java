package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.InformationTileMeasurement;

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

    @JsonProperty(value = "asset", required = false)
    private SimpleAssetDAO asset;

    @JsonProperty(value = "measurement", required = false)
    private SimpleMeasurementDAO measurement;
    
    public static InformationTileMeasurementDAOResponse create(InformationTileMeasurement tile) {
    	InformationTileMeasurementDAOResponse dao = new InformationTileMeasurementDAOResponse();
		
		dao.setId(tile.getId());
		dao.setProps(tile.getProps());

		if(tile.getMeasurement() != null) 
			dao.setMeasurement(SimpleMeasurementDAO.create(tile.getMeasurement()));
		if(tile.getMeasurement() != null) 
			dao.setAsset(SimpleAssetDAO.create(tile.getAsset()));
		
		return dao;
	}
	
	public InformationTileMeasurement mapToEntity() {
		InformationTileMeasurement tile = new InformationTileMeasurement();
		
		tile.setId(id);
		tile.setProps(props);
		
		if (asset != null) 
			tile.setAsset(asset.mapToEntity());

		if (measurement != null)
			tile.setMeasurement(measurement.mapToEntity());

		return tile;
	}
}
