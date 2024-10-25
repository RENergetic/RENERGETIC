//package com.renergetic.kpiapi.model.details;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Entity;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//import com.renergetic.common.model.Measurement;
//import org.hibernate.annotations.NotFound;
//import org.hibernate.annotations.NotFoundAction;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.renergetic.kpiapi.model.Details;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//@Entity
//@Table(name = "measurement_details")
//@RequiredArgsConstructor
//@Getter
//@Setter
//@ToString
//public class MeasurementDetails extends Details{
//	// FOREIGN KEY FROM CONNECTION TABLE
//	@ManyToOne(cascade = CascadeType.REFRESH)
//	@NotFound(action = NotFoundAction.IGNORE)
//	@JoinColumn(name = "measurement_id", nullable = false, insertable = true, updatable = true)
//	@JsonIgnore()
//	private Measurement measurement;
//
//	public MeasurementDetails(String key, String value, Long measurementId) {
//		super(key, value);
//		this.measurement = new Measurement();
//		this.measurement.setId(measurementId);
//	}
//}
