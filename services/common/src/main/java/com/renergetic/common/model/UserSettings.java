package com.renergetic.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

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