/**
 * DROP TABLE notification_messages;
 * DROP TABLE notification;
 */

package com.renergetic.hdrapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "notification_definition")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class NotificationDefinition {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "code", nullable = false, insertable = true, updatable = true, unique = true)
	private String code;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, insertable = true, updatable = true, unique = false)
	private NotificationType type;

	@Column(name = "message", nullable = false, insertable = true, updatable = true, unique = false)
	private String message;
}
