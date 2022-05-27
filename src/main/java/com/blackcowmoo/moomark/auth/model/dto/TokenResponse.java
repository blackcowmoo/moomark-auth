package com.blackcowmoo.moomark.auth.model.dto;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.Role;

import lombok.Data;

@Data
public class TokenResponse {
  public String id;
  public AuthProvider provider;
  public Role role;
}
