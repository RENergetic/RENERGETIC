package com.renergetic.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "measurement_aggregation")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class MeasurementAggregation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(cascade = {}, fetch = FetchType.EAGER, orphanRemoval = false)
	@JoinTable(
			name = "measurement_aggregation_x_measurements_aggregated",
			joinColumns =
					{ @JoinColumn(name = "aggregation_id", referencedColumnName = "id") },
			inverseJoinColumns =
					{ @JoinColumn(name = "aggregated_id", referencedColumnName = "id") }
	)
	private Set<Measurement> aggregatedMeasurements;


	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinTable(
			name = "measurement_aggregation_x_measurements_output",
			joinColumns =
					{ @JoinColumn(name = "aggregation_id", referencedColumnName = "id") },
			inverseJoinColumns =
					{ @JoinColumn(name = "output_id", referencedColumnName = "id") }
	)
	private Set<Measurement> outputMeasurements;

	//TODO: Maybe remain domain_a and domain_b to domain_in and domain_out ?
	//TODO: Check if domain is needed in the end, as probably inferred from the measurements ?
	@Column(name="domain_a")
	private Long domainA;

	//TODO: Maybe remain domain_a and domain_b to domain_in and domain_out ?
	//TODO: Check if domain is needed in the end, as probably inferred from the measurements ?
	@Column(name="domain_b")
	private Long domainB;
}
