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
public class ClientConfiguration {
    @JsonProperty(required = true)
    private String clientId;
    @JsonProperty(required = true)
    private String protocol;
    @JsonProperty(required = true)
    private boolean enabled;
    @JsonProperty(required = true)
    private boolean standardFlowEnabled;
    @JsonProperty(required = true)
    private boolean directAccessGrantsEnabled;
    @JsonProperty(required = true)
    private String rootUrl;
    @JsonProperty(required = true)
    private String baseUrl;
    @JsonProperty(required = true)
    private String adminUrl;
    @JsonProperty(required = true)
    private List<String> validRedirectUris;
    @JsonProperty(required = true)
    private List<String> webOrigins;

    @JsonProperty(required = true)
    private List<RoleConfiguration> roles;
}
