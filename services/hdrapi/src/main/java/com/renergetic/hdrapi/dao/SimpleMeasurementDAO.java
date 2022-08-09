package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.Direction;
import com.renergetic.hdrapi.model.Domain;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.model.MeasurementType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class SimpleMeasurementDAO {
	@JsonProperty(required = true, access = Access.READ_ONLY)
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

	@JsonProperty(required = false)
	private Domain domain;

	@JsonProperty(required = false)
	private Direction direction;
	
	public static SimpleMeasurementDAO create(Measurement measurement) {
		SimpleMeasurementDAO dao = new SimpleMeasurementDAO();
		
		dao.setId(measurement.getId());
		dao.setName(measurement.getName());
		dao.setSensorName(measurement.getSensorName());
		if(measurement.getType() != null) dao.setType(measurement.getType());
		dao.setLabel(measurement.getLabel());
		//dao.setDescription(measurement.getDescription());
		//dao.setIcon(measurement.getIcon());
		dao.setDomain(measurement.getDomain());
		dao.setDirection(measurement.getDirection());
		
		return dao;
	}
	
	public Measurement mapToEntity() {
		Measurement measurement = new Measurement();
		
		measurement.setId(id);
		measurement.setName(name);
		measurement.setSensorName(sensorName);
		measurement.setLabel(label);
		if (type != null)
			measurement.setType(type);
		//measurement.setDescription(description);
		//measurement.setIcon(icon);
		measurement.setDomain(domain);
		measurement.setDirection(direction);

		return measurement;
	}
}
