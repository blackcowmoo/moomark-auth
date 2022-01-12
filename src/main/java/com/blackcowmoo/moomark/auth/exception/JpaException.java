package com.blackcowmoo.moomark.auth.exception;

/**
 * JPA 관련 Exception class.
 * @author kmy20
 * @since 2022.01.12
 */
public class JpaException extends Exception {
  private static final long serialVersionUID = 118418819843250551L;
  private final int code;

  public JpaException(String msg, int code) {
    super(msg);
    this.code = code;
  }
  
  public JpaException(String msg) {
    super(msg);
    this.code = 100000;
  }
  
  public int getCode() {
    return this.code;
  }
}
