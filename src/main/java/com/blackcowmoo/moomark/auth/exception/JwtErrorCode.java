package com.blackcowmoo.moomark.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtErrorCode {
  DONT_KNOW_ERROR("DOESN'T KNOW ERROR", 100000),
  JWT_EXPIRE_TIME("JWT_EXPIRE_TIME", 200001);
  
  
  protected final String msg;
  protected final int code;
}
