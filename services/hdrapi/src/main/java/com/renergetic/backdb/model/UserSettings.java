package com.renergetic.backdb.model;

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

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_settings")
@RequiredArgsConstructor
@ToString
public class UserSettings {	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Getter
	@Setter
	@Column(name = "settings_json", nullable = false, insertable = true, updatable = true, unique = true)
	@JsonProperty(required = true)
	private String settings_json;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id", nullable = false, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private User user;

	public UserSettings(String settings_json) {
		super();
		this.settings_json = settings_json;
	}

	public void setUser(Long id) {
		this.user = new User();
		this.user.setId(id);
	}	
	
}
