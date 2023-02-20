package com.renergetic.hdrapi.model.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public class KeycloakUser   {
   private String id;
   private String username;
   private String client;
   private String token;
}


