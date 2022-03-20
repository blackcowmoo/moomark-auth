package com.blackcowmoo.moomark.auth.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.Role;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class User {

  @Id
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @Column
  private String nickname;

  @Column
  private String picture;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AuthProvider authProvider;

  @Builder
  public User(String id, String name, String email, String nickname, String picture, Role role,
      AuthProvider authProvider) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.nickname = nickname;
    this.picture = picture;
    this.role = role;
    this.authProvider = authProvider;
  }

  public User update(String name, String picture) {
    this.name = name;
    this.picture = picture;
    return this;
  }

  public User updateNickname(String nickname) {
    this.nickname = nickname;
    return this;
  }

  public String getRoleKey() {
    return this.role.getKey();
  }

  public String getAuthProviderKey() {
    return this.authProvider.getKey();
  }
}
