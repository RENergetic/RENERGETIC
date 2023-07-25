package com.renergetic.kpiapi.model.details;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renergetic.kpiapi.model.Details;
import com.renergetic.kpiapi.model.Measurement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "measurement_details")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class MeasurementDetails extends Details{
	// FOREIGN KEY FROM CONNECTION TABLE
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "measurement_id", nullable = false, insertable = true, updatable = true)
	@JsonIgnore()
	private Measurement measurement;

	public MeasurementDetails(String key, String value, Long measurementId) {
		super(key, value);
		this.measurement = new Measurement();
		this.measurement.setId(measurementId);
	}
}
