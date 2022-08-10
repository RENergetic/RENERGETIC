package com.renergetic.hdrapi.dao;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.RoleType;
import com.renergetic.hdrapi.model.User;
import com.renergetic.hdrapi.model.UserRoles;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UserRolesDAO {
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;
	
	@JsonProperty(required = true)
	private RoleType type;

	@JsonProperty(value = "update_date", required = false)
	private LocalDateTime updateDate;

	@JsonProperty(value = "user_id", required = true)
	private Long userId;
	
	public static UserRolesDAO create(UserRoles role) {
		UserRolesDAO dao = null;
		
		if (role != null) {
			dao = new UserRolesDAO();

			dao.setId(role.getId());
			dao.setType(role.getType());
			dao.setUserId(role.getUser().getId());
			dao.setUpdateDate(role.getUpdateDate());
		}
		return dao;
	}
	
	public UserRoles mapToEntity() {
		UserRoles role = new UserRoles();
		
		role.setId(id);
		role.setType(type);
		if (userId != null) {
			User user = new User();
			user.setId(userId);
			role.setUser(user);
		}
		if (updateDate == null)
			updateDate = LocalDateTime.now();
		role.setUpdateDate(updateDate);

		return role;
	}
}
