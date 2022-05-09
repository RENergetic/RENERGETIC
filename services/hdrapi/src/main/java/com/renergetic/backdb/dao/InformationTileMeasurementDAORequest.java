package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.InformationTileMeasurement;
import com.renergetic.backdb.model.Measurement;

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
    
    public static InformationTileMeasurementDAORequest create(InformationTileMeasurement tile) {
    	InformationTileMeasurementDAORequest dao = new InformationTileMeasurementDAORequest();
		
		dao.setId(tile.getId());
		dao.setProps(tile.getProps());

		if(tile.getMeasurement() != null) dao.setMeasurementId(tile.getMeasurement().getId());
		if(tile.getAsset() != null) dao.setAssetId(tile.getAsset().getId());
		
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
		return tile;
	}
}
