package com.renergetic.hdrapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "notification_messages")
@RequiredArgsConstructor
@Getter
@Setter
public class NotificationMessages {
	@Id
	@Column(name = "message", nullable = false, insertable = true, updatable = true, unique = true)
	private String message;
	
	public NotificationMessages(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return message;
	}
}
