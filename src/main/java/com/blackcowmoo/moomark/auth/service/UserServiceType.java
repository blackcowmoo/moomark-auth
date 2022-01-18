package com.blackcowmoo.moomark.auth.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserServiceType {

  NORMAL("NORMAL", 0L), OAUTH("OAUTH", 1L);

  protected final String type;
  protected final Long typeNumber;
}
