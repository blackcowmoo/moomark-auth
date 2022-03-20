package com.blackcowmoo.moomark.auth.model.entity;

import java.io.Serializable;
import java.util.Objects;

import com.blackcowmoo.moomark.auth.model.AuthProvider;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserId implements Serializable {
  private AuthProvider authProvider;
  private String id;

  public UserId(AuthProvider authProvider, String id) {
    this.authProvider = authProvider;
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserId userId = (UserId) o;
    return id.equals(userId.id) && authProvider.equals(userId.authProvider);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, authProvider);
  }
}
