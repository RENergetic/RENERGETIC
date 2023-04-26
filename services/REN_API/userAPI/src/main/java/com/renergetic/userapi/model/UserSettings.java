package com.renergetic.userapi.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_settings")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class UserSettings {	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "settings_json", nullable = false, insertable = true, updatable = true, unique = false,columnDefinition="TEXT")
	@JsonProperty(required = true)
	private String settingsJson;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id", nullable = false, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private User user;

	public UserSettings(User user,String settings_json) {
		super();
		this.user=user;
		this.settingsJson = settings_json;
	}
	
}
