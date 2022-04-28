package com.renergetic.backdb.dao;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Direction;
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
public class MeasurementDAORequest {
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;

	@JsonProperty(required = true)
	private String name;

	@JsonProperty(required = false)
	private String label;

	@JsonProperty(required = false)
	private String description;

	@JsonProperty(required = false)
	private String icon;

	@JsonProperty(required = false)
	private Long type;

	@JsonProperty(required = false)
	private Direction direction;
	
	public static MeasurementDAORequest create(Measurement measurement) {
		MeasurementDAORequest dao = null;
		
		if (measurement != null) {
			dao = new MeasurementDAORequest();
		
			dao.setId(measurement.getId());
			dao.setName(measurement.getName());
			if (measurement.getType() != null)
				dao.setType(measurement.getType().getId());
			dao.setLabel(measurement.getLabel());
			dao.setDescription(measurement.getDescription());
			dao.setIcon(measurement.getIcon());
			dao.setDirection(measurement.getDirection());
		}
		return dao;
	}
	
	public Measurement mapToEntity() {
		Measurement measurement = new Measurement();
		
		measurement.setId(id);
		measurement.setName(name);
		if (type != null) {
			MeasurementType entityType = new MeasurementType();
			entityType.setId(type);
			measurement.setType(entityType);
		}
		measurement.setLabel(label);
		measurement.setDescription(description);
		measurement.setIcon(icon);
		measurement.setDirection(direction);
		measurement.setAssets(new ArrayList<>());

		return measurement;
	}
}
