package com.renergetic.hdrapi.dao;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UserDAOResponse {
//	@JsonProperty(required = false, access = Access.READ_ONLY)
//	private Long id;

    @JsonProperty(required = true)
    private String username;
    @JsonProperty(required = true)
    private String email;
    @JsonProperty(required = false)
    private String firstName;
    @JsonProperty(required = false)
    private String lastName;

    @JsonProperty(required = true)
    private List<String> roles;

    @JsonProperty(required = true)
    private String settingsJson;

    public static UserDAOResponse create(UserRepresentation user, List<String> roles, String settingsJson) {
        UserDAOResponse dao = new UserDAOResponse();
        dao.setEmail(user.getEmail());
        dao.setFirstName(user.getFirstName());
        dao.setLastName(user.getLastName());
        dao.setUsername(user.getUsername());
        dao.setRoles(roles);
        dao.setSettingsJson(settingsJson);
        return dao;
    }


}
