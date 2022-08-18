package com.renergetic.hdrapi.dao;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UserDAORequest {
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;
	
	@JsonProperty(required = true)
	private String name;
	
	public static UserDAORequest create(User user) {
		UserDAORequest dao = null;
		
		if (user != null) {
			dao = new UserDAORequest();

			dao.setId(user.getId());
			//dao.setName(user.getName());
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