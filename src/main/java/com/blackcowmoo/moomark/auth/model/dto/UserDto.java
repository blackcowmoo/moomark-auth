package com.blackcowmoo.moomark.auth.model.dto;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.Role;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserDto {
  private String id;
  private String email;
  private String name;
  private String picture;
  private String nickname;
  private Role role;
  private AuthProvider provider;

  @Builder
  public UserDto(String id, String email, String name, String picture, AuthProvider provider) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.picture = picture;
    this.provider = provider;
  }
}
