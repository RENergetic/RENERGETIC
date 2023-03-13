package com.renergetic.measurementapi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "dashboard")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Dashboard {	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", nullable = false, insertable = true, updatable = true, unique = true)
	private String name;

	@Pattern(regexp = "https?://\\S+([/?].+)?", message = "URL isn't valid format")
	@Column(name = "url", nullable = false, insertable = true, updatable = true)
	private String url;

	@Column(name = "label", nullable = true, insertable = true, updatable = true)
	private String label;

	@Column(name = "grafana_id", nullable = true, insertable = true, updatable = true)
	private String grafanaId;

	@Column(name = "ext", nullable = true, insertable = true, updatable = true,columnDefinition="TEXT")
	private String ext;

	@OneToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "measurement_type_id", nullable = true, insertable = true, updatable = true)
	private MeasurementType measurementType;

	public Dashboard(String name, String url, String label) {
		super();
		this.name = name;
		this.url = url;
		this.label = label;
	}
}
