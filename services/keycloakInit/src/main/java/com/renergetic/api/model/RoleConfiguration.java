package com.renergetic.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class RoleConfiguration {
    @JsonProperty(required = true)
    private String name;
    @JsonProperty(required = true)
    private String description;
    @JsonProperty(required = true)
    private boolean composite;

    @JsonProperty()
    private Set<String> realmComposite;

    @JsonProperty()
    private Map<String, List<String>> clientComposite;
}
