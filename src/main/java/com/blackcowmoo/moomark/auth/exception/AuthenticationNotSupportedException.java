package com.blackcowmoo.moomark.auth.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * 인증 미지원 예외처리 클래스
 * @date 2021-12-08
 * @author kmy20
 */
public class AuthenticationNotSupportedException extends AuthenticationServiceException {

  private static final long serialVersionUID = 79136273289463177L;

  public AuthenticationNotSupportedException(String msg) {
    super(msg);
  }

}
