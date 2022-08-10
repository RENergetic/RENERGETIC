package com.renergetic.hdrapi.model;

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
@Getter
@Setter
@ToString
public class Notification {	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, insertable = true, updatable = true, unique = false)
	private NotificationType type;

	@Column(name = "message", nullable = false, insertable = true, updatable = true, unique = false)
	private String message;

	@Column(name = "icon", nullable = true, insertable = true, updatable = true, unique = false)
	private String icon;

	@Column(name = "date_from", nullable = false, insertable = true, updatable = true, unique = false)
	private LocalDateTime dateFrom;
	
	@Column(name = "date_to", nullable = true, insertable = true, updatable = true, unique = false)
	private LocalDateTime dateTo;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = true, insertable = true, updatable = true)
	private Asset asset;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "dashboard_id", nullable = true, insertable = true, updatable = true)
	private Dashboard dashboard;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "information_tile_id", nullable = true, insertable = true, updatable = true)
	private InformationTile informationTile;

	
	public Notification(NotificationType type, String message, LocalDateTime date_from, LocalDateTime date_to) {
		super();
		this.type = type;
		this.message = message;
		this.dateFrom = date_from;
		this.dateTo = date_to;
	}
	
}
