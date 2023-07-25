package com.renergetic.kpiapi.model.details;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.kpiapi.model.Details;
import com.renergetic.kpiapi.model.Measurement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tags")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class MeasurementTags extends Details{
	// FOREIGN KEY FROM CONNECTION TABLE
	@JsonProperty(required = false, access = Access.READ_ONLY)
	@ManyToMany(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinTable(
			name = "measurement_tags",
			joinColumns = @JoinColumn(name = "tag_id", nullable = true, insertable = true, updatable = true),
			inverseJoinColumns = @JoinColumn(name = "measurement_id"))
	@JsonIgnore()
	private List<Measurement> measurements;

	public MeasurementTags(String key, String value) {
		super(key, value);
		this.measurements = new ArrayList<Measurement>();
	}
}
