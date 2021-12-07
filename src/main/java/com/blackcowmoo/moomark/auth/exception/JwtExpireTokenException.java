package com.blackcowmoo.moomark.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Jwt 만료시간에 따른 예외처리 추가
 * @date 21-12-08
 * @author kmy20
 *
 */
public class JwtExpireTokenException extends AuthenticationException {

  private static final long serialVersionUID = 2355821403998752702L;
  private String token;
  
  public JwtExpireTokenException(String msg) {
    super(msg);
  }

  public JwtExpireTokenException(String token, String msg, Throwable t) {
    super(msg, t);
    this.token = token;
  }
}
