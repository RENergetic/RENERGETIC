package com.renergetic.backdb.model.details;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.renergetic.backdb.model.Details;
import com.renergetic.backdb.model.Measurement;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "measurement_details")
@RequiredArgsConstructor
@ToString
public class MeasurementDetails extends Details{
	// FOREIGN KEY FROM CONNECTION TABLE
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "measurement_id", nullable = false, insertable = true, updatable = true)
	private Measurement measurement;

	public MeasurementDetails(String key, String value, Long measurementId) {
		super(key, value);
		this.measurement = new Measurement();
		this.measurement.setId(measurementId);
	}

	public Long getMeasurement() {
		return measurement.getId();
	}

	public void setAsset(Long id) {
		this.measurement = new Measurement();
		this.measurement.setId(id);
	}
}
