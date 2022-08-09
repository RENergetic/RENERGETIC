package com.renergetic.backdb.dao;


import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.User;
import com.renergetic.backdb.model.UserRoles;
import com.renergetic.backdb.model.UserSettings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UserDAOResponse {
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;
	
	@JsonProperty(required = true)
	private String name;

	@JsonProperty(required = true)
	private List<UserRolesDAO> roles;

	@JsonProperty(required = true)
	private List<UserSettingsDAO> settings;
	
	public static UserDAOResponse create(User user, List<UserRoles> roles, List<UserSettings> settings) {
		UserDAOResponse dao = null;
		
		if (user != null) {
			dao = new UserDAOResponse();

			dao.setId(user.getId());
			//dao.setName(user.getName());
			
			if (roles != null)
				dao.setRoles(roles.stream().map(role -> UserRolesDAO.create(role)).collect(Collectors.toList()));
			
			if (settings != null)
				dao.setSettings(settings.stream().map(setting -> UserSettingsDAO.create(setting)).collect(Collectors.toList()));
		}
		return dao;
	}
	
	public User mapToEntity() {
		User user = new User();
		
		user.setId(id);
		//user.setName(name);

		return user;
	}
}
