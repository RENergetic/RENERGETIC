package com.renergetic.backdb.model;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "notification")
@RequiredArgsConstructor
@ToString
public class Notification {	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Getter
	@Setter
	@Column(name = "uuid", nullable = true, insertable = true, updatable = true, unique = true)
	private String uuid;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, insertable = true, updatable = true, unique = false)
	private NotificationType type;

	@Getter
	@Setter
	@Column(name = "message", nullable = true, insertable = true, updatable = true, unique = false)
	private String message;

	@Getter
	@Setter
	@Column(name = "date_from", nullable = false, insertable = true, updatable = true, unique = false)
	private LocalDateTime dateFrom;
	
	@Getter
	@Setter
	@Column(name = "date_to", nullable = true, insertable = true, updatable = true, unique = false)
	private LocalDateTime dateTo;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = true, insertable = true, updatable = true)
	private Asset asset;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "dashboard_id", nullable = true, insertable = true, updatable = true)
	private Dashboard dashboard;

	public Notification(String uuid, NotificationType type, String message, LocalDateTime date_from, LocalDateTime date_to) {
		super();
		this.uuid = uuid;
		this.type = type;
		this.message = message;
		this.dateFrom = date_from;
		this.dateTo = date_to;
	}

	public void setAsset(Long id) {
		this.asset = new Asset();
		this.asset.setId(id);
	}	

	public void setDashboard(Long id) {
		this.dashboard = new Dashboard();
		this.dashboard.setId(id);
	}	
	
}
