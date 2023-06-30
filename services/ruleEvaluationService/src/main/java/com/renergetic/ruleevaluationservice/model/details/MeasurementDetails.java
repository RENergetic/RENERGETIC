package com.renergetic.ruleevaluationservice.model.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renergetic.ruleevaluationservice.model.Details;
import com.renergetic.ruleevaluationservice.model.Measurement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

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
