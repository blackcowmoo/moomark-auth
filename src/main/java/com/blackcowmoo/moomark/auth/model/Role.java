package com.blackcowmoo.moomark.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

  ADMIN("ROLE_ADMIN", "관리자"), USER("ROLE_USER", "유저");

  private final String key;
  private final String title;
}
