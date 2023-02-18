package com.renergetic.hdrapi.model.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Optional;

public enum KeycloakRole implements GrantedAuthority {
    REN_GUEST("ren-guest", 0x00000000),
    REN_VISITOR("ren-visitor", 0x00000010),
    REN_USER("ren-user", 0x00000100),
    REN_MANAGER("ren-manager", 0x00001000),
    REN_TECHNICAL_MANAGER("ren-technical-manager", 0x00010000),
    REN_ADMIN("ren-admin", 0x00110000),
    REN_STAFF("ren-staff", 0x00100000),
    REN_DEV("ren-dev", 0x11111111);

    public static KeycloakRole roleByName(String name) {
        Optional<KeycloakRole> role =
                Arrays.stream(KeycloakRole.values()).filter(it -> it.name.equals(name)).findFirst();
        return role.orElse(null);

    }

    private KeycloakRole(String name, int mask) {
        this.name = name;
        this.mask = mask;
    }

    public final String name;
    public final int mask;


    @Override
    public String getAuthority() {
        return this.name;
    }
}


