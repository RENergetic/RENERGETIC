package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.User;
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
	
	@JsonProperty(value = "settings_json", required = true)
	private String settingsJson;

	@JsonProperty(value = "user_id", required = true)
	private Long userId;
	
	public static UserSettingsDAO create(UserSettings settings) {
		UserSettingsDAO dao = null;
		
		if (settings != null) {
			dao = new UserSettingsDAO();

			dao.setId(settings.getId());
			dao.setSettingsJson(settings.getSettingsJson());
			dao.setUserId(settings.getUser().getId());
		}
		return dao;
	}
	
	public UserSettings mapToEntity() {
		UserSettings role = new UserSettings();
		
		role.setId(id);
		role.setSettingsJson(settingsJson);
		if (userId != null) {
			User user = new User();
			user.setId(userId);
			role.setUser(user);
		}
		return role;
	}
}
