package com.renergetic.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

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
    @JsonProperty(required = false)
    private Boolean implicitFlowEnabled;
    @JsonProperty(required = false)
    private Boolean authorizationServicesEnabled;
    @JsonProperty(required = false)
    private Boolean publicClient;
    @JsonProperty(required = false)
    private Boolean serviceAccountEnabled;
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
    @JsonProperty(required = false)
    private Map<String, String> attributes;

    @JsonProperty(required = true)
    private List<RoleConfiguration> roles;
}
