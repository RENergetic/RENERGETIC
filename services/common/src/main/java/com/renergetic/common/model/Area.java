package com.renergetic.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "area")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Area {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, insertable = true, updatable = true, unique = true)
	private String name;

	@Column(name = "label", nullable = true, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private String label;

	@Column(name = "roi", nullable = false, insertable = true, updatable = true)
	@JsonProperty(required = true)
	private String roi;

	@Column(name = "geo_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private String geoId;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "asset_id", required = false)
	private Asset asset;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "parent_heatmap_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "parent_heatmap_id", required = false)
	private Heatmap parent;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "child_heatmap_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "child_heatmap_id", required = false)
	private Heatmap child;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "dashboard_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "dashboard_id", required = false)
	private Dashboard dashboard;

	@OneToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "uuid", nullable = false, insertable = true, updatable = false)
	private UUID uuid;
	
}
