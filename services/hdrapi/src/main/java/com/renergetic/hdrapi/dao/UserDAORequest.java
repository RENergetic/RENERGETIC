package com.renergetic.hdrapi.dao;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.User;

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
	private String firstname;
	@JsonProperty(required = false)
	private String lastname;
	@JsonProperty(required = false)
	private String password;


	public   UserRepresentation update(UserRepresentation user) {
		user.setFirstName(firstname);
		user.setLastName(lastname);

		return user;
	}
	
	public UserRepresentation mapToKeycloakEntity() {
		UserRepresentation user = new UserRepresentation();
		user.setEmail(email);
		user.setFirstName(firstname);
		user.setLastName(lastname);
		user.setUsername(username);

		return user;
	}
	public User mapToEntity() {
		User user = new User();
		user.setKeycloakId(this.id);

		return user;
	}
}
