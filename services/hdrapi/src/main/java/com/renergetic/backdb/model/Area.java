package com.renergetic.backdb.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "area")
@RequiredArgsConstructor
@ToString
public class Area {	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Getter
	@Setter
	@Column(name = "name")
	@JsonProperty(required = false)
	private String name;

	@Getter
	@Setter
	@Column(name = "label")
	@JsonProperty(required = false)
	private String label;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "asset_id", required = false)
	private Asset asset;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "parent_heatmap_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "parent_heatmap_id", required = false)
	private Heatmap parent;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "child_heatmap_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "child_heatmap_id", required = false)
	private Heatmap child;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "dashboard_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "dashboard_id", required = false)
	private Dashboard dashboard;

	public void setAsset(Long id) {
		this.asset = new Asset();
		this.asset.setId(id);
	}

	public void setParent(Long id) {
		this.parent = new Heatmap();
		this.parent.setId(id);
	}

	public void setChild(Long id) {
		this.child = new Heatmap();
		this.child.setId(id);
	}

	public void setDashboard(Long id) {
		this.dashboard = new Dashboard();
		this.dashboard.setId(id);
	}
	
}
