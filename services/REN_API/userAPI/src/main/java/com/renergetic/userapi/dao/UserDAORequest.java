package com.renergetic.userapi.dao;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.keycloak.representations.idm.UserRepresentation;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UserDAORequest {


	@JsonProperty(required = true)
	private String username;
	@JsonProperty(required = true)
	private String email;
	@JsonProperty(required = false)
	private String id;
	@JsonProperty(required = false)
	private String firstName;
	@JsonProperty(required = false)
	private String lastName;
	@JsonProperty(required = false)
	private String password;


	public   UserRepresentation update(UserRepresentation user) {
		user.setFirstName(firstName);
		user.setLastName(lastName);
		if(user.getEmail()==null ||user.getEmail().isEmpty()){
			user.setEmail(email);
		}
		return user;
	}
	
	public UserRepresentation mapToKeycloakEntity() {
		UserRepresentation user = new UserRepresentation();
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);

		return user;
	}
	public User mapToEntity() {
		User user = new User();
		user.setKeycloakId(this.id);

		return user;
	}
}
