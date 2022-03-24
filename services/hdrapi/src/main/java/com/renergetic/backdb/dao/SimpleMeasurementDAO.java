package com.renergetic.backdb.dao;

import com.renergetic.backdb.model.Direction;
import com.renergetic.backdb.model.Measurement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class SimpleMeasurementDAO {
	private Long id;
	
	private String name;
	
	private String label;
	
	private String description;
	
	private String icon;
	
	private String type;
	
	private Direction direction;
	
	public static SimpleMeasurementDAO create(Measurement measurement) {
		SimpleMeasurementDAO dao = new SimpleMeasurementDAO();
		
		dao.setId(measurement.getId());
		dao.setName(measurement.getName());
		if (measurement.getType() != null)
			dao.setType(measurement.getType().getName());
		dao.setLabel(measurement.getLabel());
		dao.setDescription(measurement.getDescription());
		dao.setIcon(measurement.getIcon());
		dao.setDirection(measurement.getDirection());
		
		return dao;
	}
	
	public Measurement mapToEntity() {
		Measurement measurement = new Measurement();
		
		measurement.setId(id);
		measurement.setName(name);
		measurement.setLabel(label);
		measurement.setDescription(description);
		measurement.setIcon(icon);
		measurement.setDirection(direction);

		return measurement;
	}
}
