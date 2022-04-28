package com.renergetic.backdb.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

	@Column(name = "ext", nullable = true, insertable = true, updatable = true)
	private String ext;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id", nullable = true, insertable = true, updatable = true)
	private User user;

	@OneToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "uuid", nullable = false, insertable = true, updatable = false)
	private UUID uuid;

	public Dashboard(String name, String url, String label) {
		super();
		this.name = name;
		this.url = url;
		this.label = label;
	}
}
