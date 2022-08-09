package com.renergetic.hdrapi.model.details;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.renergetic.hdrapi.model.Details;
import com.renergetic.hdrapi.model.Measurement;

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
	private Measurement measurement;

	public MeasurementDetails(String key, String value, Long measurementId) {
		super(key, value);
		this.measurement = new Measurement();
		this.measurement.setId(measurementId);
	}
}
