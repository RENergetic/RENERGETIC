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
			/*name="measurement_aggr_measurement",
			joinColumns = @JoinColumn( name="measurement_aggr_id"),
			inverseJoinColumns = @JoinColumn( name="measurement_id")*/
	)
	private List<Measurement> aggregatedMeasurements;


	@OneToMany(cascade = {}, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinTable(
			/*name="measurement_aggr_measurement",
			joinColumns = @JoinColumn( name="measurement_aggr_id"),
			inverseJoinColumns = @JoinColumn( name="measurement_id")*/
	)
	private List<Measurement> outputMeasurements;

	//TODO: Maybe remain domain_a and domain_b to domain_in and domain_out ?
	//TODO: Check if domain is needed in the end, as probably inferred from the measurements ?
	@Column(name="domain_a")
	@Enumerated(EnumType.STRING)
	private Domain domainA;

	//TODO: Maybe remain domain_a and domain_b to domain_in and domain_out ?
	//TODO: Check if domain is needed in the end, as probably inferred from the measurements ?
	@Column(name="domain_b")
	@Enumerated(EnumType.STRING)
	private Domain domainB;
}
