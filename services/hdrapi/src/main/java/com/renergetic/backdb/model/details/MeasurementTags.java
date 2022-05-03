package com.renergetic.backdb.model.details;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.renergetic.backdb.model.Details;
import com.renergetic.backdb.model.Measurement;

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
	@ManyToMany(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinTable(
			name = "measurement_tags",
			joinColumns = @JoinColumn(name = "tag_id", nullable = true, insertable = true, updatable = true),
			inverseJoinColumns = @JoinColumn(name = "measurement_id"))
	private List<Measurement> measurements;

	public MeasurementTags(String key, String value) {
		super(key, value);
		this.measurements = new ArrayList<Measurement>();
	}
}
