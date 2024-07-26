package com.renergetic.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class RealmConfiguration {
    @JsonProperty(required = true)
    private String realm;
    @JsonProperty(required = true)
    private boolean registrationAllowed;
    @JsonProperty(required = true)
    private List<ClientConfiguration> clients;
    @JsonProperty(required = true)
    private List<UserConfiguration> users;
}
