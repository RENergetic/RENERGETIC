/**
 * DROP TABLE notification_messages;
 * DROP TABLE notification;
 */

package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "notification_definition")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class NotificationDefinition {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, insertable = true, updatable = true, unique = true)
	private String code;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, insertable = true, updatable = true, unique = false)
	private NotificationType type;

	@Column(name = "message", nullable = false, insertable = true, updatable = true, unique = false)
	private String message;
}
