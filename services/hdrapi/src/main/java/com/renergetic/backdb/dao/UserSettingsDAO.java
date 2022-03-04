package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.UserSettings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UserSettingsDAO {
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;
	
	@JsonProperty(required = true)
	private String settings_json;

	@JsonProperty(required = true)
	private Long user_id;
	
	public static UserSettingsDAO create(UserSettings settings) {
		UserSettingsDAO dao = null;
		
		if (settings != null) {
			dao = new UserSettingsDAO();

			dao.setId(settings.getId());
			dao.setSettings_json(settings.getSettings_json());
			dao.setUser_id(settings.getUser().getId());
		}
		return dao;
	}
	
	public UserSettings mapToEntity() {
		UserSettings role = new UserSettings();
		
		role.setId(id);
		role.setSettings_json(settings_json);
		role.setUser(user_id);

		return role;
	}
}
