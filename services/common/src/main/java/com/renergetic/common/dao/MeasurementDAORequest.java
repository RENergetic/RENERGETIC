package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.common.model.Direction;
import com.renergetic.common.model.Domain;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.MeasurementType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class MeasurementDAORequest {
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;

	@JsonProperty(required = true)
	private String name;

	@JsonProperty(value = "sensor_name", required = true)
	private String sensorName;

	@JsonProperty(required = false)
	private String label;

	@JsonProperty(required = false)
	private String description;

	@JsonProperty(required = false)
	private String icon;

	@JsonProperty(required = false)
	private MeasurementType type;

	@JsonProperty(value = "asset", required = false)
	private SimpleAssetDAO asset;

	@JsonProperty(required = false)
	private Domain domain;

	@JsonProperty(required = false)
	private Direction direction;
	
	public static MeasurementDAORequest create(Measurement measurement) {
		MeasurementDAORequest dao = null;
		
		if (measurement != null) {
			dao = new MeasurementDAORequest();
		
			dao.setId(measurement.getId());
			dao.setName(measurement.getName());
			dao.setSensorName(measurement.getSensorName());
			if (measurement.getType() != null)
				dao.setType(measurement.getType() );
			dao.setLabel(measurement.getLabel());
			//dao.setDescription(measurement.getDescription());
			//dao.setIcon(measurement.getIcon());
			dao.setDomain(measurement.getDomain());
			dao.setDirection(measurement.getDirection());
			if (measurement.getAsset() != null)
				dao.setAsset(SimpleAssetDAO.create(measurement.getAsset()));
		}
		return dao;
	}
	
	public Measurement mapToEntity() {
		Measurement measurement = new Measurement();
		
		measurement.setId(id);
		measurement.setName(name);
		if (type != null) {
			measurement.setType(type);
		}
		measurement.setLabel(label);
		//measurement.setDescription(description);
		//measurement.setIcon(icon);
		measurement.setDomain(domain);
		measurement.setDirection(direction);
		measurement.setSensorName(sensorName);
		if (asset != null) {
			measurement.setAsset(asset.mapToEntity());
		}

		return measurement;
	}
}