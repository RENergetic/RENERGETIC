package com.renergetic.userapi.model.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KeycloakUser   {
   private String id;
   private String username;
   private String client;
   private String token;
}


