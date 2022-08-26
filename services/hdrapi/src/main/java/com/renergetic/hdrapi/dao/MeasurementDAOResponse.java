package com.renergetic.hdrapi.dao;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.hdrapi.dao.details.MeasurementDetailsDAO;
import com.renergetic.hdrapi.model.Direction;
import com.renergetic.hdrapi.model.Domain;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.model.MeasurementType;
import com.renergetic.hdrapi.model.details.MeasurementDetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class MeasurementDAOResponse {
	@JsonProperty(required = false)
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

	@JsonProperty(value = "measurement_details",required = false)
	private List<MeasurementDetailsDAO> measurementDetails;

	public static MeasurementDAOResponse create(Measurement measurement, List<MeasurementDetails> details) {
		MeasurementDAOResponse dao = null;

		if (measurement != null) {
			dao = new MeasurementDAOResponse();

			dao.setId(measurement.getId());
			dao.setName(measurement.getName());
			dao.setSensorName(measurement.getSensorName());
			if (measurement.getType() != null)
				dao.setType(measurement.getType());
			dao.setLabel(measurement.getLabel());
			//dao.setDescription(measurement.getDescription());
			//dao.setIcon(measurement.getIcon());
			dao.setDomain(measurement.getDomain());
			dao.setDirection(measurement.getDirection());

			if (details != null) {
				List<MeasurementDetailsDAO> detailsDao = details.stream().map(
						it -> new MeasurementDetailsDAO(it.getKey(), it.getValue(), it.getMeasurement().getId())).collect(
						Collectors.toList());
				dao.setMeasurementDetails(detailsDao);
			}

		}
		return dao;
	}

	public Measurement mapToEntity() {
		Measurement measurement = new Measurement();

		measurement.setId(id);
		measurement.setName(name);
		measurement.setSensorName(sensorName);
		if (type != null)
			measurement.setType(type);
		measurement.setLabel(label);
		//measurement.setDescription(description);
		//measurement.setIcon(icon);
		measurement.setDomain(domain);
		measurement.setDirection(direction);

		return measurement;
	}
}