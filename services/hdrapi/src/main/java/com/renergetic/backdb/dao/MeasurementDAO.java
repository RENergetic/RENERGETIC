package com.renergetic.backdb.dao;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Direction;
import com.renergetic.backdb.model.Measurement;
import com.renergetic.backdb.model.MeasurementType;
import com.renergetic.backdb.model.details.MeasurementDetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class MeasurementDAO {
	@Getter
	@Setter
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;

	@Getter
	@Setter
	@JsonProperty(required = true)
	private String name;

	@Getter
	@Setter
	@JsonProperty(required = false)
	private String label;

	@Getter
	@Setter
	@JsonProperty(required = false)
	private String description;

	@Getter
	@Setter
	@JsonProperty(required = false)
	private String icon;

	@Getter
	@JsonProperty(required = false)
	private MeasurementType type;

	@Getter
	@Setter
	@JsonProperty(required = false)
	private Direction direction;

	@Getter
	@Setter
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private List<MeasurementDetails> measurement_details;

	public void setType(Long type_id) {
		if (type_id != null) {
			this.type = new MeasurementType();
			this.type.setId(type_id);
		}
	}

	public void type(MeasurementType type) {
		this.type = type;
	}
	
	public static MeasurementDAO create(Measurement measurement, List<MeasurementDetails> details) {
		MeasurementDAO dao = new MeasurementDAO();
		
		dao.setId(measurement.getId());
		dao.setName(measurement.getName());
		if (measurement.getType() != null)
			dao.type(measurement.getType());
		dao.setLabel(measurement.getLabel());
		dao.setDescription(measurement.getDescription());
		dao.setIcon(measurement.getIcon());
		
		if (details != null)
			dao.setMeasurement_details(details);
		
		return dao;
	}
	
	public Measurement mapToEntity() {
		Measurement measurement = new Measurement();
		
		measurement.setId(id);
		measurement.setName(name);
		if (type != null) measurement.setType(type.getId());
		measurement.setLabel(label);
		measurement.setDescription(description);
		measurement.setIcon(icon);
		measurement.setDirection(direction);
		measurement.setAssets(new ArrayList<>());

		return measurement;
	}
}
