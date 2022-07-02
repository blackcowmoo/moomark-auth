package com.blackcowmoo.moomark.auth.model.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.Role;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@IdClass(UserId.class)
public class User implements Serializable {

  @Id
  @Enumerated(EnumType.STRING)
  private AuthProvider authProvider;

  @Id
  private String id;

  @Column(nullable = false)
  private String email;

  @Column
  private String nickname;

  @Column
  private String picture;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Builder
  public User(String id, AuthProvider authProvider, String email, String nickname, String picture,
      Role role) {
    this.id = id;
    this.email = email;
    this.nickname = nickname;
    this.picture = picture;
    this.role = role;
    this.authProvider = authProvider;
  }

  public User updateNickname(String nickname) {
    this.nickname = nickname;
    return this;
  }

  public User updatePicture(String picture) {
    this.picture = picture;
    return this;
  }
}
