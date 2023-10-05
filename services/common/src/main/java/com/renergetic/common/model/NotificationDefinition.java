/**
 * DROP TABLE notification_messages;
 * DROP TABLE notification;
 */

package com.renergetic.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
