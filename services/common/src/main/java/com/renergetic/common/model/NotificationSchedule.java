package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_schedule")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class NotificationSchedule {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * notification valid from, value occurs at
	 */
	@Column(name = "date_from", nullable = false, insertable = true, updatable = true, unique = false)
	private LocalDateTime dateFrom;
	
	@Column(name = "date_to", nullable = true, insertable = true, updatable = true, unique = false)
	private LocalDateTime dateTo;

	//time the prediction was made
	@Column(name = "notification_timestamp", nullable = true, insertable = true, updatable = true, unique = false)
	private LocalDateTime notificationTimestamp;

	@ManyToOne(cascade = CascadeType.REFRESH, optional = false)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "definition_id", nullable = false, insertable = true, updatable = true)
	private NotificationDefinition definition;

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


	@Column(name = "notification_value", nullable = true, insertable = true, updatable = true, unique = false)
	private Double notificationValue;
	@ManyToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "measurement_id", nullable = true, insertable = true, updatable = true)
	private Measurement measurement;

}
