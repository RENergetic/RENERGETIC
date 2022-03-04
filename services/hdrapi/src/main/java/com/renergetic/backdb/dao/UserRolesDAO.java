package com.renergetic.backdb.dao;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.RoleType;
import com.renergetic.backdb.model.UserRoles;

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

	@JsonProperty(required = false)
	private LocalDateTime update_date;

	@JsonProperty(required = true)
	private Long user_id;
	
	public static UserRolesDAO create(UserRoles role) {
		UserRolesDAO dao = null;
		
		if (role != null) {
			dao = new UserRolesDAO();

			dao.setId(role.getId());
			dao.setType(role.getType());
			dao.setUser_id(role.getUser().getId());
			dao.setUpdate_date(role.getUpdate_date());
		}
		return dao;
	}
	
	public UserRoles mapToEntity() {
		UserRoles role = new UserRoles();
		
		role.setId(id);
		role.setType(type);
		role.setUser(user_id);
		if (update_date == null)
			update_date = LocalDateTime.now();
		role.setUpdate_date(update_date);

		return role;
	}
}
