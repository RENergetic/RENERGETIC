package com.renergetic.hdrapi.service.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
public class KeycloakUser   {
   private String id;
   private String username;
   private String client;
}


