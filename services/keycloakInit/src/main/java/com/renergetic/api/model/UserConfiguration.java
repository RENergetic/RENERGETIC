package com.renergetic.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class UserConfiguration {
    @JsonProperty()
    private String email;
    @JsonProperty(required = true)
    private String username;
    @JsonProperty()
    private String firstname;
    @JsonProperty()
    private String lastname;
    @JsonProperty(required = true)
    private boolean enabled;
    @JsonProperty()
    private boolean emailVerified;
    @JsonProperty(required = true)
    private boolean passwordTemporary;
    @JsonProperty(required = true)
    private String passwordType;
    @JsonProperty(required = true)
    private String passwordValue;
    @JsonProperty(required = true)
    private UserRoleConfiguration roles;

}
