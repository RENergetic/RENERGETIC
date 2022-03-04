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
	@Column(name = "content", nullable = false, insertable = true, updatable = true, unique = false)
	private String content;

	@Getter
	@Setter
	@Column(name = "value", nullable = true, insertable = true, updatable = true, unique = false)
	private String value;

	@Getter
	@Setter
	@Column(name = "timestamp", nullable = false, insertable = true, updatable = true, unique = false)
	private LocalDateTime timestamp;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = false, insertable = true, updatable = true)
	private Asset asset;

	public Notification(String uuid, NotificationType type, String content, String value, LocalDateTime timestamp) {
		super();
		this.uuid = uuid;
		this.type = type;
		this.content = content;
		this.value = value;
		this.timestamp = timestamp;
	}

	public void setAsset(Long id) {
		this.asset = new Asset();
		this.asset.setId(id);
	}	
	
}
