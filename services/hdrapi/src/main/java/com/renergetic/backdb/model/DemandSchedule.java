package com.renergetic.backdb.model;

import java.time.LocalDateTime;

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


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "demand_schedule")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class DemandSchedule {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "demand_start", nullable = false, insertable = true, updatable = true)
	private LocalDateTime demand_start;

	@Column(name = "demand_stop", nullable = true, insertable = true, updatable = true)
	private LocalDateTime demand_stop;

	@Column(name = "update_date", nullable = true, insertable = true, updatable = true)
	private LocalDateTime update;

	// FOREIGN KEY FROM DEMAND TABLE
	@ManyToOne(optional = true, cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "demand_id", nullable = false, insertable = true, updatable = true)
	private DemandRequest demand;
}
