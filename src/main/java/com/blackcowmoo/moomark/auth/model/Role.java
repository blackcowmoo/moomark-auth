package com.blackcowmoo.moomark.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

  ADMIN("ADMIN", "관리자"), USER("USER", "유저");

  private final String key;
  private final String title;
}
