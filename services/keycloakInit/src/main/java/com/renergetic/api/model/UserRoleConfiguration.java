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
public class UserRoleConfiguration {
    @JsonProperty()
    private List<String> realm;
    @JsonProperty()
    private Map<String, List<String>> clients;
}
